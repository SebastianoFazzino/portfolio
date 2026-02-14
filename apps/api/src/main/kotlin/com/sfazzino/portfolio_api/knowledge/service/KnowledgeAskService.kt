package com.sfazzino.portfolio_api.knowledge.service

import com.sfazzino.portfolio_api.knowledge.KnowledgeChunkRepository
import com.sfazzino.portfolio_api.knowledge.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.knowledge.dtos.KnowledgeChunkDto
import com.sfazzino.portfolio_api.knowledge.dtos.TokenChunk
import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.llm.prompt.LlmPrompts
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Service
class KnowledgeAskService(
    private val llmClient: LlmClient,
    private val prompts: LlmPrompts,
    private val knowledgeRepository: KnowledgeChunkRepository,
    @Value($$"${contact.rag-chunk-size}") private val chunkSize: Int
) {

    fun ask(question: String): KnowledgeAskResponse {
        log.info("[ASK] Question: {}", question)

        val queryEmbedding = llmClient.embed(question)
        val chunks = knowledgeRepository.findTopSimilar(queryEmbedding = queryEmbedding, limit = chunkSize)

        val context = chunks.joinToString(separator = "\n\n") { it.content }
        val prompt = buildPrompt(question, context)

        val answer = llmClient.generate(prompt)

        logFullAnswer("[ASK]", question, answer)
        return KnowledgeAskResponse(answer = answer.trim())
    }

    fun askStream(question: String, emitter: SseEmitter) {
        log.info("[ASK-STREAM] Question: {}", question)

        Thread {
            val cancelled = AtomicBoolean(false)
            val cancelLogged = AtomicBoolean(false)

            var heartbeat: ScheduledFuture<*>? = null
            val firstChunkSent = AtomicBoolean(false)
            val fullAnswer = StringBuilder(2048)

            fun logCancel(reason: String) {
                if (cancelLogged.compareAndSet(false, true)) {
                    log.info(
                        "[ASK-STREAM] CANCELLED reason={} charsSoFar={} question='{}'",
                        reason,
                        fullAnswer.length,
                        question
                    )
                }
            }

            emitter.onCompletion {
                cancelled.set(true)
                logCancel("client_disconnected")
            }
            emitter.onTimeout {
                cancelled.set(true)
                logCancel("timeout")
            }
            emitter.onError { ex ->
                cancelled.set(true)
                logCancel("error:${ex.javaClass.simpleName}")
            }

            try {
                val queryEmbedding = llmClient.embed(question)
                if (cancelled.get()) return@Thread

                emit(emitter, "status", "Searching knowledge base…", cancelled, ::logCancel)
                val chunks = knowledgeRepository.findTopSimilar(queryEmbedding, chunkSize)
                if (cancelled.get()) return@Thread

                emit(emitter, "status", "Building context…", cancelled, ::logCancel)
                val context = chunks.joinToString("\n\n") { it.content }
                val prompt = buildPrompt(question, context)

                emit(emitter, "status", "Generating answer", cancelled, ::logCancel)

                heartbeat = sseScheduler.scheduleAtFixedRate({
                    if (!firstChunkSent.get() && !cancelled.get()) {
                        emit(emitter, "status", "Generating answer", cancelled, ::logCancel)
                    }
                }, HEARTBEAT_MS, HEARTBEAT_MS, TimeUnit.MILLISECONDS)

                llmClient.generateStream(prompt, cancelled::get) { chunk ->
                    if (chunk.isEmpty()) return@generateStream

                    fullAnswer.append(chunk)

                    if (firstChunkSent.compareAndSet(false, true)) {
                        heartbeat?.cancel(false)
                    }

                    emit(emitter, "token", TokenChunk(chunk), cancelled, ::logCancel)
                }

                if (!cancelled.get()) {
                    emit(emitter, "done", "ok", cancelled, ::logCancel)
                    emitter.complete()
                }
            } catch (e: Exception) {
                if (!cancelled.get()) {
                    log.warn("[ASK-STREAM] failed", e)
                    emit(emitter, "error", e.message ?: "An unexpected error occurred.", cancelled, ::logCancel)
                    emitter.completeWithError(e)
                }
            } finally {

                heartbeat?.cancel(false)
                val answerText = fullAnswer.toString().trim()
                if (answerText.isNotEmpty()) {
                    logFullAnswer("[ASK-STREAM]", question, answerText)
                } else {
                    log.info("[ASK-STREAM] no tokens produced (cancelled={})", cancelLogged.get())
                }
            }

        }.apply { isDaemon = true }.start()
    }

    private fun emit(
        emitter: SseEmitter,
        eventName: String,
        data: Any,
        cancelled: AtomicBoolean,
        logCancel: (String) -> Unit,
    ) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data))
        } catch (_: Exception) {
            cancelled.set(true)
            logCancel("send_failed")
        }
    }

    private fun buildPrompt(question: String, context: String): String {
        return prompts.rag
            .replace("{{CONTEXT}}", context.trim())
            .replace("{{QUESTION}}", question.trim())
    }

    fun getAllChunksRaw(): List<KnowledgeChunkDto> {
        return knowledgeRepository.findAll()
    }

    private fun logFullAnswer(tag: String, question: String, answer: String) {
        log.info(
            "{} full answer (chars={}): question='{}'\n{}",
            tag, answer.length, question, answer.trim()
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(KnowledgeAskService::class.java)
        private val sseScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        private const val HEARTBEAT_MS = 1200L
    }
}