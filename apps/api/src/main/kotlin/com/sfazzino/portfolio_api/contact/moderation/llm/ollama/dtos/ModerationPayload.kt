package com.sfazzino.portfolio_api.contact.moderation.llm.ollama.dtos

data class ModerationPayload(
    val verdict: String,
    val reason: String? = null
)
