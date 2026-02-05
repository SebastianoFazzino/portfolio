package com.sfazzino.portfolio_api.rag.service

import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.rag.repository.KnowledgeChunkSearchRepository
import org.springframework.stereotype.Service

@Service
class KnowledgeAskService(
    private val llmClient: LlmClient,
    private val knowledgeRepository: KnowledgeChunkSearchRepository
) {

    fun ask(question: String): KnowledgeAskResponse {
        val queryEmbedding = llmClient.embed(question)

        val chunks = knowledgeRepository.findTopSimilar(
            queryEmbedding = queryEmbedding,
            limit = 6
        )

        val context = chunks.joinToString(separator = "\n\n") { it.content }
        val prompt = buildPrompt(question, context)

        val answer = llmClient.generate(prompt)
        return KnowledgeAskResponse(answer = answer.trim())
    }

    private fun buildPrompt(question: String, context: String): String {
        return """
            You are answering questions about Sebastiano Fazzino.
            Use only the CONTEXT to answer the QUESTION.
            If the answer is not in the context, say: "I don't have that information."

            CONTEXT:
            $context

            QUESTION:
            $question
        """.trimIndent()
    }
}
