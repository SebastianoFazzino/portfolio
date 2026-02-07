package com.sfazzino.portfolio_api.llm.ollama.dtos

data class ModerationPayload(
    val verdict: String,
    val reason: String? = null
)
