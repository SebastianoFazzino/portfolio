package com.sfazzino.portfolio_api.rag.dtos

import jakarta.validation.constraints.NotBlank

data class KnowledgeIngestRequest(
    @field:NotBlank
    val source: String,
    @field:NotBlank
    val content: String
)