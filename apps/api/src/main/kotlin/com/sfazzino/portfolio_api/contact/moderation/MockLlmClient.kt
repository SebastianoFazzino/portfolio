package com.sfazzino.portfolio_api.contact.moderation

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MockLlmClient : LlmClient {

  override fun moderate(message: String): ModerationDecision {
    // Mock behavior for now:
    // - Always allow
    return ModerationDecision.allow()

    // if (message.contains("BLOCKME", ignoreCase = true)) return ModerationDecision.block("mock_block")
    // return ModerationDecision.allow()
  }
}
