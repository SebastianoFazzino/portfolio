package com.sfazzino.portfolio_api.llm.ollama.dtos

data class OllamaEmbeddingsRequest(
    val model: String,
    val prompt: String
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
