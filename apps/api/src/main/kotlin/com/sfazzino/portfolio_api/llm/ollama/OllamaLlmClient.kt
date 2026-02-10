package com.sfazzino.portfolio_api.llm.ollama

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision
import com.sfazzino.portfolio_api.contact.moderation.ModerationVerdict
import com.sfazzino.portfolio_api.exception.ApplicationException.Companion.unknownError
import com.sfazzino.portfolio_api.llm.LlmClient
import com.sfazzino.portfolio_api.llm.ollama.dtos.*
import com.sfazzino.portfolio_api.llm.prompt.LlmPrompts
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.databind.json.JsonMapper
import java.util.concurrent.atomic.AtomicLong

@Component
@Profile("ollama")
class OllamaLlmClient(
  private val prompts: LlmPrompts,
  private val jsonMapper: JsonMapper,
  private val properties: OllamaProperties,
): LlmClient {

  private val lastWarmupAt = AtomicLong(0L)
  private val restClient = RestClient.create(properties.baseUrl)

  init {
    warmUp()
    log.info("Ollama LLM Moderation Client initialized with model='{}'", properties.moderationModel)
  }

  override fun moderate(message: String): ModerationDecision {
    val prompt = composePrompt(message)

    return try {
      val rawContent = generateRawText(
        model = properties.moderationModel,
        prompt = prompt,
        temperature = 0.0,
        numCtx = properties.moderationContext,
      )

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

  override fun embed(text: String): FloatArray {
    val request = OllamaEmbeddingsRequest(
      model = properties.ragEmbeddingModel,
      prompt = text.trim()
    )

    val response = restClient.post()
      .uri(EMBEDDINGS_ENDPOINT)
      .contentType(MediaType.APPLICATION_JSON)
      .body(request)
      .retrieve()
      .body<OllamaEmbeddingsResponse>()
      ?: throw unknownError("Ollama embeddings empty response")

    return response.embedding
  }

  override fun generate(prompt: String): String {
    val rawText = generateRawText(
      model = properties.ragGenerationModel,
      prompt = prompt.trim(),
      temperature = 0.5,
      numCtx = properties.ragContext,
    )

    if (rawText.isBlank()) throw unknownError("Ollama generated empty content")
    return rawText.trim()
  }

  override fun generateStream(prompt: String, onToken: (String) -> Unit) {
    generateRawStream(
      model = properties.ragGenerationModel,
      prompt = prompt.trim(),
      temperature = 0.5,
      numCtx = properties.ragContext,
      onToken = onToken,
    )
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

  private fun composePrompt(message: String): String {
    return """
      ${prompts.moderation}

      ${message.trim()}
    """.trimIndent()
  }

  private fun buildGenerateRequest(
    model: String,
    prompt: String,
    temperature: Double,
    numCtx: Int,
    stream: Boolean,
    maxTokens: Int = properties.maxTokens,
    keepAlive: String = properties.keepAlive,
  ): OllamaGenerateRequest {
    return OllamaGenerateRequest(
      model = model,
      prompt = prompt,
      stream = stream,
      format = null,
      keepAlive = keepAlive,
      options = OllamaOptions(
        temperature = temperature,
        numPredict = maxTokens,
        numCtx = numCtx,
      )
    )
  }

  private fun generateRawText(
    model: String,
    prompt: String,
    temperature: Double,
    numCtx: Int,
    maxTokens: Int = properties.maxTokens,
    keepAlive: String = properties.keepAlive,
  ): String {
    val request = buildGenerateRequest(
      model = model,
      prompt = prompt,
      temperature = temperature,
      numCtx = numCtx,
      stream = false,
      maxTokens = maxTokens,
      keepAlive = keepAlive,
    )

    val response = restClient.post()
      .uri(GENERATE_ENDPOINT)
      .contentType(MediaType.APPLICATION_JSON)
      .body(request)
      .retrieve()
      .body<OllamaGenerateResponse>()
      ?: throw unknownError("Ollama generate empty response")

    return response.response?.trim().orEmpty()
  }

  private fun generateRawStream(
    model: String,
    prompt: String,
    temperature: Double,
    numCtx: Int,
    onToken: (String) -> Unit,
    maxTokens: Int = properties.maxTokens,
    keepAlive: String = properties.keepAlive,
  ) {
    val request = buildGenerateRequest(
      model = model,
      prompt = prompt,
      temperature = temperature,
      numCtx = numCtx,
      stream = true,
      maxTokens = maxTokens,
      keepAlive = keepAlive,
    )

    restClient.post()
      .uri(GENERATE_ENDPOINT)
      .contentType(MediaType.APPLICATION_JSON)
      .body(request)
      .exchange { _, response ->
        val input = response.body ?: throw unknownError("Ollama stream empty body")

        input.bufferedReader().useLines { lines ->
          lines.forEach { line ->
            if (line.isBlank()) return@forEach

            val chunk = jsonMapper.readValue(line, OllamaGenerateStreamChunk::class.java)

            val text = chunk.response.orEmpty()
            if (text.isNotBlank()) onToken(text)

            if (chunk.done == true) return@useLines
          }
        }
      }
  }

  companion object {
    private val log = LoggerFactory.getLogger(OllamaLlmClient::class.java)

    private const val GENERATE_ENDPOINT = "/api/generate"
    private const val EMBEDDINGS_ENDPOINT = "/api/embeddings"

    private const val REASON_ERROR = "llm_error"
    private const val REASON_EMPTY_CONTENT = "llm_empty_content"
    private const val REASON_INVALID_VERDICT = "llm_invalid_verdict"
  }
}