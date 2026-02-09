package com.sfazzino.portfolio_api.rag.service

import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.llm.prompt.LlmPrompts
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeChunkDto
import com.sfazzino.portfolio_api.rag.repository.KnowledgeChunkRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KnowledgeAskService(
    private val llmClient: LlmClient,
    private val prompts: LlmPrompts,
    private val knowledgeRepository: KnowledgeChunkRepository
) {

    fun ask(question: String): KnowledgeAskResponse {
        log.info("Question: $question")
        val queryEmbedding = llmClient.embed(question)

        val chunks = knowledgeRepository.findTopSimilar(
            queryEmbedding = queryEmbedding,
            limit = 10
        )

        val context = chunks.joinToString(separator = "\n\n") { it.content }
        val prompt = buildPrompt(question, context)

        val answer = llmClient.generate(prompt)

        log.info("Generated answer: $answer")
        return KnowledgeAskResponse(answer = answer.trim())
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
    }
}
