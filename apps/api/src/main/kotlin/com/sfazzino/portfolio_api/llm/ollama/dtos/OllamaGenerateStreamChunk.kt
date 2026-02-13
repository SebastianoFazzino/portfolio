package com.sfazzino.portfolio_api.llm.ollama.dtos

data class OllamaGenerateStreamChunk(
    val response: String? = null,
    val done: Boolean? = null,
  )
