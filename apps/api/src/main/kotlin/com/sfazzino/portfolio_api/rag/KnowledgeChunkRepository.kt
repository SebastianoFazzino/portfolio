package com.sfazzino.portfolio_api.rag

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface KnowledgeChunkRepository: JpaRepository<KnowledgeChunk, UUID> {
    fun existsByContentHash(contentHash: String): Boolean
}