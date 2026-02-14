package com.sfazzino.portfolio_api.knowledge.dtos

import java.util.*

data class KnowledgeChunkRow(
    val id: UUID,
    val source: String,
    val content: String,
    val contentHash: String
)