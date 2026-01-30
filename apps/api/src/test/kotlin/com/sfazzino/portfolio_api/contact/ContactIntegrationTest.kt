package com.sfazzino.portfolio_api.contact

import com.sfazzino.portfolio_api.TestcontainersConfiguration
import com.sfazzino.portfolio_api.exception.ErrorCodes.EXPIRED_API_KEY
import com.sfazzino.portfolio_api.exception.ErrorCodes.MISSING_API_KEY
import com.sfazzino.portfolio_api.security.api_key.ApiKey
import com.sfazzino.portfolio_api.security.api_key.ApiKeyAuthFilter
import com.sfazzino.portfolio_api.security.api_key.ApiKeyHasher
import com.sfazzino.portfolio_api.security.api_key.ApiKeyRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
class ContactIntegrationTest {

  @Autowired lateinit var mvc: MockMvc
  @Autowired lateinit var apiKeyRepository: ApiKeyRepository

  @MockitoBean lateinit var emailService: ContactEmailService

  @BeforeEach
  fun setup() {
    whenever(emailService.send(any(), any(), any(), any())).thenReturn(true)
    apiKeyRepository.deleteAll()
  }

  @AfterEach
  fun cleanup() {
    apiKeyRepository.deleteAll()
  }

  @Test
  fun `POST contact without api key returns 401`() {
    mvc.post("/contact") {
      contentType = MediaType.APPLICATION_JSON
      content = """{"name":"A","email":"a@b.com","message":"Hi","website":""}"""
    }.andExpect {
      status { isUnauthorized() }
      content { json("""{"error":"unauthorized","code":${MISSING_API_KEY}}""") }
    }
  }

  @Test
  fun `POST contact with valid api key and scope returns 200`() {
    val rawKey = "dev-test-key"
    val hashed = ApiKeyHasher.hash(rawKey)

    apiKeyRepository.save(
      ApiKey(
        key = hashed,
        client = "integration-test",
        scopes = setOf("contact:write"),
        expiresAt = Instant.now().plusSeconds(3600),
      )
    )

    mvc.post("/contact") {
      header(ApiKeyAuthFilter.API_KEY_HEADER, rawKey)
      contentType = MediaType.APPLICATION_JSON
      content = """{"name":"Seb","email":"seb@example.com","message":"Hello","website":""}"""
    }.andExpect {
      status { isOk() }
      jsonPath("$.ok") { value(true) }
    }
  }

  @Test
  fun `POST contact with valid api key but missing scope returns 403`() {
    val rawKey = "dev-test-key"
    val hashed = ApiKeyHasher.hash(rawKey)

    apiKeyRepository.save(
      ApiKey(
        key = hashed,
        client = "integration-test",
        scopes = setOf("wrong.scope:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    mvc.post("/contact") {
      header(ApiKeyAuthFilter.API_KEY_HEADER, rawKey)
      contentType = MediaType.APPLICATION_JSON
      content = """{"name":"Seb","email":"seb@example.com","message":"Hello","website":""}"""
    }.andExpect {
      status { isForbidden() }
      jsonPath("$.error") { value("forbidden") }
      jsonPath("$.required_scope") { value("contact:write") }
    }
  }

  @Test
  fun `POST contact with expired api key returns 401`() {
    val rawKey = "dev-test-key"
    val hashed = ApiKeyHasher.hash(rawKey)

    apiKeyRepository.save(
      ApiKey(
        key = hashed,
        client = "integration-test",
        scopes = setOf("contact:write"),
        expiresAt = Instant.now().minusSeconds(10)
      )
    )

    mvc.post("/contact") {
      header(ApiKeyAuthFilter.API_KEY_HEADER, rawKey)
      contentType = MediaType.APPLICATION_JSON
      content = """{"name":"Seb","email":"seb@example.com","message":"Hello","website":""}"""
    }.andExpect {
      status { isUnauthorized() }
      content { json("""{"error":"unauthorized","code":${EXPIRED_API_KEY}}""") }
    }
  }
}
