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
    log.info(
      "Ollama LLM Client initialized (baseUrl='{}') with moderationModel='{}', ragGenerationModel='{}', ragEmbeddingModel='{}'",
      properties.baseUrl,
      properties.moderationModel,
      properties.ragGenerationModel,
      properties.ragEmbeddingModel,
    )
  }

  override fun moderate(message: String): ModerationDecision {
    val prompt = composePrompt(message)

    return try {
      val rawContent = generateRawText(
        model = properties.moderationModel,
        prompt = prompt,
        temperature = 0.0,
        maxTokens = properties.moderationMaxTokens,
        numCtx = properties.moderationContext,
        format = "json",
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
      prompt = text.trim(),
      options = OllamaEmbeddingOptions(
        numCtx = properties.ragContext
      )
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
      maxTokens = properties.ragMaxTokens,
      numCtx = properties.ragContext,
      format = null,
    )

    if (rawText.isBlank()) throw unknownError("Ollama generated empty content")
    return rawText.trim()
  }

  override fun generateStream(prompt: String, isCancelled: () -> Boolean, onToken: (String) -> Unit) {
    generateRawStream(
      model = properties.ragGenerationModel,
      prompt = prompt.trim(),
      temperature = 0.5,
      numCtx = properties.ragContext,
      isCancelled = isCancelled,
      onToken = onToken,
    )
  }

  override fun warmUp() {
    val now = System.currentTimeMillis()
    val last = lastWarmupAt.get()

    if (now - last < properties.warmUpIntervalMs) return
    if (!lastWarmupAt.compareAndSet(last, now)) return

    try {
      ping()
      log.info("Ollama LLM warm-up completed")
    } catch (error: Exception) {
      log.warn("Ollama LLM warm-up failed", error)
    }
  }

  private fun ping() {
    embed("warmup")
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
    maxTokens: Int,
    format: String?,
    keepAlive: String = properties.keepAlive,
  ): OllamaGenerateRequest {
    return OllamaGenerateRequest(
      model = model,
      prompt = prompt,
      stream = stream,
      format = format,
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
    maxTokens: Int,
    format: String?,
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
      format = format,
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
    isCancelled: () -> Boolean,
    onToken: (String) -> Unit,
    maxTokens: Int = properties.ragMaxTokens,
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
      format = null,
    )

    restClient.post()
      .uri(GENERATE_ENDPOINT)
      .contentType(MediaType.APPLICATION_JSON)
      .body(request)
      .exchange { _, response ->
        val input = response.body ?: throw unknownError("Ollama stream empty body")

        val cancelWatcher = Thread {
          while (!isCancelled()) {
            try { Thread.sleep(50) } catch (_: InterruptedException) { return@Thread }
          }
          runCatching { input.close() }
        }.apply { isDaemon = true }

        cancelWatcher.start()

        try {
          input.bufferedReader().use { reader ->
            while (true) {
              val line = runCatching { reader.readLine() }
                .getOrNull() ?: return@exchange

              if (line.isBlank()) continue

              val chunk = runCatching {
                jsonMapper.readValue(line, OllamaGenerateStreamChunk::class.java)
              }.getOrNull() ?: continue

              chunk.response
                ?.takeIf { it.isNotBlank() }
                ?.let(onToken)

              if (chunk.done == true) return@exchange
            }
          }
        } finally {
          cancelWatcher.interrupt()
          runCatching { input.close() }
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