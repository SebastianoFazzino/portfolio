package com.sfazzino.portfolio_api.rag.service

import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.llm.prompt.LlmPrompts
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.rag.repository.KnowledgeChunkSearchRepository
import org.springframework.stereotype.Service

@Service
class KnowledgeAskService(
    private val llmClient: LlmClient,
    private val prompts: LlmPrompts,
    private val knowledgeRepository: KnowledgeChunkSearchRepository
) {

    fun ask(question: String): KnowledgeAskResponse {
        val queryEmbedding = llmClient.embed(question)

        val chunks = knowledgeRepository.findTopSimilar(
            queryEmbedding = queryEmbedding,
            limit = 10
        )

        val context = chunks.joinToString(separator = "\n\n") { it.content }
        val prompt = buildPrompt(question, context)

        val answer = llmClient.generate(prompt)
        return KnowledgeAskResponse(answer = answer.trim())
    }

    private fun buildPrompt(question: String, context: String): String {
        return prompts.rag
            .replace("{{CONTEXT}}", context.trim())
            .replace("{{QUESTION}}", question.trim())
    }
}
