package com.sfazzino.portfolio_api.knowledge.dtos

import jakarta.validation.constraints.NotBlank

data class KnowledgeAskRequest(
    @field:NotBlank val question: String
)