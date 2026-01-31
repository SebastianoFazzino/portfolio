package com.sfazzino.portfolio_api.contact.moderation.llm.ollama

import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision
import com.sfazzino.portfolio_api.contact.moderation.ModerationVerdict
import com.sfazzino.portfolio_api.contact.moderation.llm.LlmClient
import com.sfazzino.portfolio_api.contact.moderation.llm.ollama.dtos.ModerationPayload
import com.sfazzino.portfolio_api.contact.moderation.llm.ollama.dtos.OllamaGenerateRequest
import com.sfazzino.portfolio_api.contact.moderation.llm.ollama.dtos.OllamaGenerateResponse
import com.sfazzino.portfolio_api.contact.moderation.llm.ollama.dtos.OllamaOptions
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.databind.json.JsonMapper

@Component
@Profile("llm")
class OllamaLlmClient(
  resourceLoader: ResourceLoader,
  private val jsonMapper: JsonMapper,
  private val properties: OllamaModerationProperties
): LlmClient {

  private val restClient = RestClient.create(properties.baseUrl)

  init {
    promptMessage = resourceLoader.getResource(properties.promptPath)
      .inputStream
      .bufferedReader()
      .use { it.readText() }
      .trim()

    log.info("Ollama LLM Client initialized with model='{}'", properties.model)
  }

  override fun moderate(message: String): ModerationDecision {
    val prompt = composePrompt(message)

    val request = OllamaGenerateRequest(
      model = properties.model,
      prompt = prompt,
      stream = false,
      format = "json",
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

  companion object {
    lateinit var promptMessage: String
    private val log = LoggerFactory.getLogger(OllamaLlmClient::class.java)

    private const val REASON_ERROR = "llm_error"
    private const val REASON_EMPTY_RESPONSE = "llm_empty_response"
    private const val REASON_EMPTY_CONTENT = "llm_empty_content"
    private const val REASON_INVALID_VERDICT = "llm_invalid_verdict"
  }
}