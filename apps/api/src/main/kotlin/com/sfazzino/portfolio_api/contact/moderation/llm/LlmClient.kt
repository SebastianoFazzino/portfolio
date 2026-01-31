package com.sfazzino.portfolio_api.contact.moderation.llm

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision

interface LlmClient {
  fun moderate(message: String): ModerationDecision
}
