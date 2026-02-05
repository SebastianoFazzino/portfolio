package com.sfazzino.portfolio_api.llm.ollama

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "llm.ollama")
data class OllamaProperties(
  val baseUrl: String,
  val moderationModel: String,
  val ragGenerationModel: String,
  val ragEmbeddingModel: String,
  val maxTokens: Int,
  val warmUpIntervalMs: Long,
  val contactPromptPath: String,
)