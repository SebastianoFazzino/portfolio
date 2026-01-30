package com.sfazzino.portfolio_api.contact.moderation

interface LlmClient {
  fun moderate(message: String): ModerationDecision
}
