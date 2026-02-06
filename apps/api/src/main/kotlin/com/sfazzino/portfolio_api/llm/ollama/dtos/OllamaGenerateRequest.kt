package com.sfazzino.portfolio_api.llm.ollama.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OllamaGenerateRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean,
    val format: String?,
    @field:JsonProperty("keep_alive")
    val keepAlive: String,
    val options: OllamaOptions,
)

data class OllamaOptions(
    val temperature: Double,
    @field:JsonProperty("num_predict")
    val numPredict: Int,
    @field:JsonProperty("num_ctx")
    val numCtx: Int
)