package com.sfazzino.portfolio_api.contact.moderation

import org.springframework.stereotype.Service

@Service
class LlmModerationService(
  private val llmClient: LlmClient
) : ModerationService {

  override fun check(message: String): ModerationDecision {
    val trimmed = message.trim()
    if (trimmed.isEmpty()) return ModerationDecision.block("empty_message")
    return llmClient.moderate(trimmed)
  }
}
