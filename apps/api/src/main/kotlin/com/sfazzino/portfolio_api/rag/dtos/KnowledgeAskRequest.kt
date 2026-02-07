package com.sfazzino.portfolio_api.rag.dtos

import jakarta.validation.constraints.NotBlank

data class KnowledgeAskRequest(
    @field:NotBlank val question: String
)