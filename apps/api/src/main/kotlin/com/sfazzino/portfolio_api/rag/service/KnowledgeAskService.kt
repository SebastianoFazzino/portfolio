package com.sfazzino.portfolio_api.rag.service

import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.llm.prompt.LlmPrompts
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeChunkDto
import com.sfazzino.portfolio_api.rag.dtos.TokenChunk
import com.sfazzino.portfolio_api.rag.repository.KnowledgeChunkRepository
import org.slf4j.LoggerFactory
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
    private val knowledgeRepository: KnowledgeChunkRepository
) {

    fun ask(question: String): KnowledgeAskResponse {
        log.info("[ASK] Question: {}", question)

        val queryEmbedding = llmClient.embed(question)
        val chunks = knowledgeRepository.findTopSimilar(queryEmbedding = queryEmbedding, limit = CHUNK_SIZE)

        val context = chunks.joinToString(separator = "\n\n") { it.content }
        val prompt = buildPrompt(question, context)

        val answer = llmClient.generate(prompt)

        log.info("[ASK] Generated answer length={}", answer.length)
        return KnowledgeAskResponse(answer = answer.trim())
    }

    fun askStream(question: String, emitter: SseEmitter) {
        log.info("[ASK-STREAM] Question: {}", question)

        Thread {
            val cancelled = AtomicBoolean(false)
            var heartbeat: ScheduledFuture<*>? = null
            val firstChunkSent = AtomicBoolean(false)

            emitter.onCompletion {
                cancelled.set(true)
                log.info("[ASK-STREAM] client disconnected")
            }
            emitter.onTimeout {
                cancelled.set(true)
                log.info("[ASK-STREAM] stream timeout")
            }

            try {
                val queryEmbedding = llmClient.embed(question)
                if (cancelled.get()) return@Thread

                emit(emitter, "status", "Searching knowledge base…")
                val chunks = knowledgeRepository.findTopSimilar(queryEmbedding, CHUNK_SIZE)
                if (cancelled.get()) return@Thread

                emit(emitter, "status", "Building context…")
                val context = chunks.joinToString("\n\n") { it.content }
                val prompt = buildPrompt(question, context)

                emit(emitter, "status", "Generating answer")

                heartbeat = sseScheduler.scheduleAtFixedRate({
                    if (!firstChunkSent.get() && !cancelled.get()) {
                        emit(emitter, "status", "Generating answer")
                    }
                }, HEARTBEAT_MS, HEARTBEAT_MS, TimeUnit.MILLISECONDS)

                llmClient.generateStream(prompt) { chunk ->
                    if (cancelled.get()) return@generateStream
                    if (chunk.isEmpty()) return@generateStream

                    if (firstChunkSent.compareAndSet(false, true)) {
                        heartbeat?.cancel(false)
                    }

                    log.debug("[ASK-STREAM] token='{}'", chunk)
                    emit(emitter, "token", TokenChunk(chunk))
                }

                if (!cancelled.get()) {
                    emit(emitter, "done", "ok")
                    emitter.complete()
                }
            } catch (e: Exception) {
                if (!cancelled.get()) {
                    log.warn("[ASK-STREAM] failed", e)
                    emit(emitter, "error", e.message ?: "An unexpected error occurred.")
                    emitter.completeWithError(e)
                }
            } finally {
                heartbeat?.cancel(false)
            }
        }.apply { isDaemon = true }.start()
    }

    private fun emit(emitter: SseEmitter, eventName: String, data: Any) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data))
        } catch (_: Exception) {
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

    companion object {
        private val log = LoggerFactory.getLogger(KnowledgeAskService::class.java)
        private val sseScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

        private const val CHUNK_SIZE = 6
        private const val HEARTBEAT_MS = 1200L
    }
}
