package com.sfazzino.portfolio_api.contact.moderation.llm.ollama

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "contact.moderation.ollama")
data class OllamaModerationProperties(
  val baseUrl: String,
  val model: String,
  val maxTokens: Int,
  val promptPath: String,
  val warmUpIntervalMs: Long,
)