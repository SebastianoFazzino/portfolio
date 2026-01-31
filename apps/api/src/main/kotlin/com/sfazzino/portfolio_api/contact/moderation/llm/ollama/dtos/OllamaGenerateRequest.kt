package com.sfazzino.portfolio_api.contact.moderation.llm.ollama.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OllamaGenerateRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean,
    val format: String,
    val options: OllamaOptions
)

data class OllamaOptions(
    val temperature: Double,
    @field:JsonProperty("num_predict")
    val numPredict: Int
)