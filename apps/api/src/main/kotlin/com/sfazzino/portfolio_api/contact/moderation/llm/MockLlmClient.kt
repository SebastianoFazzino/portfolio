package com.sfazzino.portfolio_api.contact.moderation.llm

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!llm")
class MockLlmClient : LlmClient {

  override fun moderate(message: String): ModerationDecision {
    return ModerationDecision.allow()
  }
}
