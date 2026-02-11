package com.sfazzino.portfolio_api.llm.ollama

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "llm.ollama")
data class OllamaProperties(
  val baseUrl: String,
  val moderationModel: String,
  val ragGenerationModel: String,
  val ragEmbeddingModel: String,
  val moderationMaxTokens: Int,
  val ragMaxTokens: Int,
  val moderationContext: Int,
  val ragContext: Int,
  val keepAlive: String,
  val warmUpIntervalMs: Long
)