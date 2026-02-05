package com.sfazzino.portfolio_api.llm.ollama

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision
import com.sfazzino.portfolio_api.contact.moderation.ModerationVerdict
import com.sfazzino.portfolio_api.exception.ApplicationException.Companion.unknownError
import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.llm.ollama.dtos.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.databind.json.JsonMapper
import java.util.concurrent.atomic.AtomicLong

@Component
@Profile("llm")
class OllamaLlmClient(
  resourceLoader: ResourceLoader,
  private val jsonMapper: JsonMapper,
  private val properties: OllamaProperties,
): LlmClient {

  private val lastWarmupAt = AtomicLong(0L)
  private val restClient = RestClient.create(properties.baseUrl)

  init {
    promptMessage = resourceLoader.getResource(properties.contactPromptPath)
      .inputStream
      .bufferedReader()
      .use { it.readText() }
      .trim()

    // Ping the LLM service with a warm-up request
    warmUp()

    log.info("Ollama LLM Moderation Client initialized with model='{}'", properties.moderationModel)
  }

  override fun moderate(message: String): ModerationDecision {
    val prompt = composePrompt(message)

    val request = OllamaGenerateRequest(
      model = properties.moderationModel,
      prompt = prompt,
      stream = false,
      format = "json",
      keepAlive = "10m",
      options = OllamaOptions(
        temperature = 0.0,
        numPredict = properties.maxTokens
      )
    )

    return try {
      val response = restClient.post()
        .uri("/api/generate")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body<OllamaGenerateResponse>()
        ?: return ModerationDecision.block(REASON_EMPTY_RESPONSE)

      val rawContent = response.response?.trim().orEmpty()
      if (rawContent.isEmpty()) return ModerationDecision.block(REASON_EMPTY_CONTENT)

      val payload = jsonMapper.readValue(rawContent, ModerationPayload::class.java)
      val verdict = runCatching { ModerationVerdict.valueOf(payload.verdict) }
        .getOrElse { return ModerationDecision.block(REASON_INVALID_VERDICT) }

      ModerationDecision(verdict = verdict, reason = payload.reason)
    } catch (ex: Exception) {
      log.warn("LLM moderation failed: blocking message", ex)
      ModerationDecision.block(REASON_ERROR)
    }
  }

  private fun composePrompt(message: String): String {
    return """
      $promptMessage

      ${message.trim()}
    """.trimIndent()
  }

  override fun embed(text: String): FloatArray {
    val request = OllamaEmbeddingsRequest(
      model = properties.ragEmbeddingModel,
      prompt = text.trim()
    )

    val response = restClient.post()
      .uri("/api/embeddings")
      .contentType(MediaType.APPLICATION_JSON)
      .body(request)
      .retrieve()
      .body<OllamaEmbeddingsResponse>()
      ?: throw unknownError("Ollama embeddings empty response")

    return response.embedding
  }

  override fun warmUp() {
    val now = System.currentTimeMillis()
    val last = lastWarmupAt.get()

    if (now - last < properties.warmUpIntervalMs) return
    if (!lastWarmupAt.compareAndSet(last, now)) return

    try {
      moderate("Hello, world!")
      log.info("Ollama LLM warm-up completed")

    } catch (error: Exception) {
      log.warn("Ollama LLM warm-up failed", error)
    }
  }

  companion object {
    lateinit var promptMessage: String
    private val log = LoggerFactory.getLogger(OllamaLlmClient::class.java)

    private const val REASON_ERROR = "llm_error"
    private const val REASON_EMPTY_RESPONSE = "llm_empty_response"
    private const val REASON_EMPTY_CONTENT = "llm_empty_content"
    private const val REASON_INVALID_VERDICT = "llm_invalid_verdict"
  }
}