package com.sfazzino.portfolio_api.security

import com.sfazzino.portfolio_api.TestcontainersConfiguration
import com.sfazzino.portfolio_api.contact.ContactService
import com.sfazzino.portfolio_api.knowledge.dtos.KnowledgeAskResponse
import com.sfazzino.portfolio_api.knowledge.service.KnowledgeAskService
import com.sfazzino.portfolio_api.security.api_key.ApiKey
import com.sfazzino.portfolio_api.security.api_key.ApiKeyAuthFilter.Companion.API_KEY_HEADER
import com.sfazzino.portfolio_api.security.api_key.ApiKeyRepository
import com.sfazzino.portfolio_api.security.cors.CorsProperties
import com.sfazzino.portfolio_api.security.crypto.CryptoUtil
import com.sfazzino.portfolio_api.security.rate_limiter.ContactRateLimitProps
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SecuritySmokeTest {

  @Autowired lateinit var mvc: MockMvc
  @MockitoBean lateinit var corsProperties: CorsProperties
  @MockitoBean lateinit var buildProperties: BuildProperties
  @MockitoBean lateinit var contactService: ContactService
  @MockitoBean lateinit var apiKeyRepository: ApiKeyRepository
  @MockitoBean lateinit var rateLimiterProps: ContactRateLimitProps
  @MockitoBean lateinit var knowledgeAskService: KnowledgeAskService

  @BeforeEach
  fun setup() {
    whenever(corsProperties.allowedOrigins).thenReturn(listOf("http://localhost:3000"))
    whenever(rateLimiterProps.maxRequests).thenReturn(1)
    whenever(rateLimiterProps.windowMs).thenReturn(10000)
  }

  @Test
  fun `GET healthz is public (no api key)`() {
    whenever(buildProperties.version).thenReturn("test")

    mvc.get("/healthz") {
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
      jsonPath("$.status") { value("OK") }
      jsonPath("$.version") { value("test") }
    }
  }

  @Test
  fun `POST contact without api key returns 401`() {
    doNothing().`when`(contactService).handle(any())

    mvc.post("/contact") {
      contentType = MediaType.APPLICATION_JSON
      content = """{"name":"A","email":"a@b.com","message":"Hi","website":""}"""
    }.andExpect {
      status { isUnauthorized() }
    }
  }

  @Test
  fun `POST contact with valid api key + scope returns 200`() {
    doNothing().whenever(contactService).handle(any())

    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("contact:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.post("/contact") {
      header(API_KEY_HEADER, rawKey)
      contentType = MediaType.APPLICATION_JSON
      content = """{"name":"A","email":"a@b.com","message":"Hi","website":""}"""
    }.andExpect {
      status { isOk() }
      jsonPath("$.ok") { value(true) }
    }
  }

  @Test
  fun `POST contact with valid api key but missing scope returns 403`() {
    doNothing().whenever(contactService).handle(any())

    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("out.of.scope:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.post("/contact") {
      header(API_KEY_HEADER, rawKey)
      contentType = MediaType.APPLICATION_JSON
      content = """{"name":"A","email":"a@b.com","message":"Hi","website":""}"""
    }.andExpect {
      status { isForbidden() }
    }
  }

  @Test
  fun `GET knowledge ask with contact scope returns 200`() {
    whenever(knowledgeAskService.ask(any())).thenReturn(KnowledgeAskResponse("ok"))

    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("contact:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.get("/knowledge/ask") {
      header(API_KEY_HEADER, rawKey)
      contentType = MediaType.APPLICATION_JSON
      content = """{"question":"Who is Sebastiano?"}"""
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
    }
  }

  @Test
  fun `GET knowledge ask with missing scope returns 403`() {
    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("out.of.scope:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.get("/knowledge/ask") {
      header(API_KEY_HEADER, rawKey)
      contentType = MediaType.APPLICATION_JSON
      content = """{"question":"Who is Sebastiano?"}"""
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isForbidden() }
    }

    verifyNoInteractions(knowledgeAskService)
  }

  @Test
  fun `GET knowledge ask stream with valid api key + contact scope returns 200`() {
    doNothing().whenever(knowledgeAskService).askStream(any(), any())

    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("contact:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.get("/knowledge/ask/stream") {
      header(API_KEY_HEADER, rawKey)
      param("question", "Who is Sebastiano?")
      accept = MediaType.TEXT_EVENT_STREAM
    }.andExpect {
      status { isOk() }
      request { asyncStarted() }
    }
  }

  @Test
  fun `GET knowledge ask stream with valid api key but missing scope returns 403`() {
    doNothing().whenever(knowledgeAskService).askStream(any(), any())

    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("out.of.scope:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.get("/knowledge/ask/stream") {
      header(API_KEY_HEADER, rawKey)
      param("question", "Who is Sebastiano?")
      accept = MediaType.TEXT_EVENT_STREAM
    }.andExpect {
      status { isForbidden() }
    }
  }

  @Test
  fun `GET knowledge chunks with contact scope returns 403`() {
    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("contact:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.get("/knowledge/chunks-raw") {
      header(API_KEY_HEADER, rawKey)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isForbidden() }
    }

    verifyNoInteractions(knowledgeAskService)
  }

  @Test
  fun `GET rag chunks with admin scope - success`() {
    whenever(knowledgeAskService.getAllChunksRaw()).thenReturn(listOf())

    val rawKey = "dev-test-key"
    val hashed = CryptoUtil.hash(rawKey)

    whenever(apiKeyRepository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "test-client",
        scopes = setOf("admin:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.get("/knowledge/chunks-raw") {
      header(API_KEY_HEADER, rawKey)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk() }
    }
  }
}
