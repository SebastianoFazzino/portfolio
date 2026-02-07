package com.sfazzino.portfolio_api.llm

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!ollama")
class MockLlmClient : LlmClient {

  override fun moderate(message: String): ModerationDecision {
    return ModerationDecision.allow()
  }

  override fun embed(text: String): FloatArray {
    return FloatArray(384) { 0.0f }
  }

  override fun generate(prompt: String): String {
    return "Lorem ipsum"
  }

  override fun warmUp() {}
}
