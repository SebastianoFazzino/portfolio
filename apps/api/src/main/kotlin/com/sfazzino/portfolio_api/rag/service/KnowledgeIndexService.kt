package com.sfazzino.portfolio_api.rag.service

import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.rag.KnowledgeChunk
import com.sfazzino.portfolio_api.rag.repository.KnowledgeChunkRepository
import com.sfazzino.portfolio_api.security.crypto.CryptoUtil.hash
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KnowledgeIndexService(
    private val llmClient: LlmClient,
    private val knowledgeRepository: KnowledgeChunkRepository
) {
    fun ingestText(source: String, text: String) {
        val chunks = chunkText(text)

        var storedChunksCount = 0

        chunks.forEach { chunk ->
            val contentHash = hash("$source::$chunk")

            if (knowledgeRepository.existsByContentHash(contentHash)) {
                return@forEach
            }

            val embedding = llmClient.embed(chunk)

            knowledgeRepository.save(
                KnowledgeChunk(
                    source = source,
                    content = chunk,
                    contentHash = contentHash,
                    embedding = embedding
                )
            )

            storedChunksCount++
        }

        log.info(
            "Indexed chunks source={} totalChunks={} storedChunks={}",
            source, chunks.size, storedChunksCount
        )
    }

    private fun chunkText(text: String): List<String> {
        val cleanedText = text.replace("\r\n", "\n").trim()
        if (cleanedText.length <= 1200) return listOf(cleanedText)

        val chunkSize = 1200
        val overlapSize = 200

        val chunks = mutableListOf<String>()
        var startIndex = 0

        while (startIndex < cleanedText.length) {
            val endIndex = (startIndex + chunkSize).coerceAtMost(cleanedText.length)
            val chunk = cleanedText.substring(startIndex, endIndex).trim()
            if (chunk.isNotBlank()) chunks.add(chunk)
            if (endIndex == cleanedText.length) break
            startIndex = (endIndex - overlapSize).coerceAtLeast(0)
        }

        return chunks
    }

    companion object {
        private val log = LoggerFactory.getLogger(KnowledgeIndexService::class.java)
    }
}
