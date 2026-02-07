package com.sfazzino.portfolio_api.rag.dtos

import java.util.*

data class KnowledgeChunkRow(
    val id: UUID,
    val source: String,
    val content: String
)