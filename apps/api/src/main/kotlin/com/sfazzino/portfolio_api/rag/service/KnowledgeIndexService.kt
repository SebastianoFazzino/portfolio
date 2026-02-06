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
        val cleaned = text.replace("\r\n", "\n").trim()
        if (cleaned.isBlank()) return emptyList()

        var currentSection = "General"
        val chunks = mutableListOf<String>()

        cleaned.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { line ->
                if (isTitleLine(line)) {
                    currentSection = normalizeTitle(line)
                    return@forEach
                }

                val normalized = "$currentSection | ${line.trim()}"

                if (normalized.length <= CHUNK_SIZE) {
                    chunks.add(normalized)
                } else {
                    chunks.addAll(splitWithOverlap(normalized))
                }
            }

        return chunks
    }

    private fun isTitleLine(line: String): Boolean {
        val s = line.trim()

        // Accept both "Backend" and "Backend:" as titles
        val candidate = s.removeSuffix(":").trim()
        if (candidate.length !in 2..40) return false

        // Titles should not look like sentences
        if (candidate.any { it == '.' || it == '?' || it == '!' }) return false

        // Titles should be mostly letters/spaces (allow & and -)
        val allowed = candidate.all { it.isLetter() || it.isWhitespace() || it == '&' || it == '-' }
        return allowed
    }

    private fun normalizeTitle(line: String): String {
        return line.trim().removeSuffix(":").trim().ifBlank { "General" }
    }

    private fun splitWithOverlap(text: String): List<String> {
        val chunks = mutableListOf<String>()
        var start = 0

        while (start < text.length) {
            val end = (start + CHUNK_SIZE).coerceAtMost(text.length)
            val part = text.substring(start, end).trim()
            if (part.isNotBlank()) chunks.add(part)
            if (end == text.length) break
            start = (end - OVERLAP_SIZE).coerceAtLeast(0)
        }
        return chunks
    }

    companion object {
        private val log = LoggerFactory.getLogger(KnowledgeIndexService::class.java)
        private const val CHUNK_SIZE = 800
        private const val OVERLAP_SIZE = 100
    }
}
