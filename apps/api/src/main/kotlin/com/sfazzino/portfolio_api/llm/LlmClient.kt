package com.sfazzino.portfolio_api.llm

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision

interface LlmClient {
  fun warmUp()
  fun embed(text: String): FloatArray
  fun generate(prompt: String): String
  fun moderate(message: String): ModerationDecision
}
