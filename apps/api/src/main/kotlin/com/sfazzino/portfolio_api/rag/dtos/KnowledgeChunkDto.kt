package com.sfazzino.portfolio_api.rag.dtos

import java.time.Instant
import java.util.*

data class KnowledgeChunkDto(
    val id: UUID,
    val source: String,
    val content: String,
    val contentHash: String,
    val createdBy: String?,
    val createdAt: Instant,
    val lastModifiedBy: String?,
    val lastModifiedAt: Instant?
)