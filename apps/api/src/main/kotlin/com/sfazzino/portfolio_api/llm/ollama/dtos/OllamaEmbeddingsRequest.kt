package com.sfazzino.portfolio_api.llm.ollama.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OllamaEmbeddingsRequest(
    val model: String,
    val prompt: String,
    val options: OllamaEmbeddingOptions
)

data class OllamaEmbeddingOptions(
    @field:JsonProperty("num_ctx")
    val numCtx: Int
)

data class OllamaEmbeddingsResponse(
    val embedding: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OllamaEmbeddingsResponse

        if (!embedding.contentEquals(other.embedding)) return false

        return true
    }

    override fun hashCode(): Int {
        return embedding.contentHashCode()
    }
}
