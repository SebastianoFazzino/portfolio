package com.sfazzino.portfolio_api.security.api_key

import com.sfazzino.portfolio_api.exception.ErrorCodes.EXPIRED_API_KEY
import com.sfazzino.portfolio_api.exception.ErrorCodes.INVALID_API_KEY
import com.sfazzino.portfolio_api.exception.ErrorCodes.MISSING_API_KEY
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.time.Instant

class ApiKeyAuthFilterTest {

  private val chain: FilterChain = mock()
  private val repository: ApiKeyRepository = mock()
  private val filter = ApiKeyAuthFilter(repository)

  @AfterEach
  fun cleanup() {
    SecurityContextHolder.clearContext()
  }

  @Test
  fun `shouldNotFilter - OPTIONS is skipped`() {
    val req = MockHttpServletRequest("OPTIONS", "/anything")
    val res = MockHttpServletResponse()

    filter.doFilter(req, res, chain)

    verify(chain).doFilter(any(), any())
  }

  @Test
  fun `shouldNotFilter - GET healthz is skipped`() {
    val req = MockHttpServletRequest("GET", "/healthz")
    val res = MockHttpServletResponse()

    filter.doFilter(req, res, chain)

    verify(chain).doFilter(any(), any())
  }

  @Test
  fun `missing api key - returns 401 with code MISSING_API_KEY`() {
    val req = MockHttpServletRequest("POST", "/contact")
    val res = MockHttpServletResponse()

    filter.doFilter(req, res, chain)

    assertEquals(401, res.status)
    assertEquals("""{"error":"unauthorized","code":"$MISSING_API_KEY"}""", res.contentAsString)

    verify(chain, never()).doFilter(any(), any())
    assertNull(SecurityContextHolder.getContext().authentication)
  }

  @Test
  fun `invalid api key - returns 401 with code INVALID_API_KEY`() {
    val rawKey = "nope"
    whenever(repository.findByKey(ApiKeyHasher.hash(rawKey))).thenReturn(null)

    val req = MockHttpServletRequest("POST", "/contact").apply {
      addHeader(ApiKeyAuthFilter.API_KEY_HEADER, rawKey)
    }
    val res = MockHttpServletResponse()

    filter.doFilter(req, res, chain)

    assertEquals(401, res.status)
    assertEquals("""{"error":"unauthorized","code":"$INVALID_API_KEY"}""", res.contentAsString)

    verify(chain, never()).doFilter(any(), any())
    assertNull(SecurityContextHolder.getContext().authentication)
  }

  @Test
  fun `expired api key - returns 401 with code EXPIRED_API_KEY`() {
    val rawKey = "expired-key"
    val hashed = ApiKeyHasher.hash(rawKey)

    whenever(repository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "client",
        scopes = setOf("contact:write"),
        expiresAt = Instant.now().minusSeconds(60)
      )
    )

    val req = MockHttpServletRequest("POST", "/contact").apply {
      addHeader(ApiKeyAuthFilter.API_KEY_HEADER, rawKey)
    }
    val res = MockHttpServletResponse()

    filter.doFilter(req, res, chain)

    assertEquals(401, res.status)
    assertEquals("""{"error":"unauthorized","code":"$EXPIRED_API_KEY"}""", res.contentAsString)

    verify(chain, never()).doFilter(any(), any())
    assertNull(SecurityContextHolder.getContext().authentication)
  }

  @Test
  fun `missing required scope - returns 403 with required_scope`() {
    val rawKey = "valid-but-wrong-scope"
    val hashed = ApiKeyHasher.hash(rawKey)

    whenever(repository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "client",
        scopes = setOf("wrong.scope:*"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    val req = MockHttpServletRequest("POST", "/contact").apply {
      addHeader(ApiKeyAuthFilter.API_KEY_HEADER, rawKey)
    }
    val res = MockHttpServletResponse()

    filter.doFilter(req, res, chain)

    assertEquals(403, res.status)
    assertEquals("""{"error":"forbidden","required_scope":"contact:write"}""", res.contentAsString)

    verify(chain, never()).doFilter(any(), any())
    assertNull(SecurityContextHolder.getContext().authentication)
  }

  @Test
  fun `valid key and scope - sets authentication and continues chain`() {
    val rawKey = "valid-key"
    val hashed = ApiKeyHasher.hash(rawKey)

    whenever(repository.findByKey(hashed)).thenReturn(
      ApiKey(
        key = hashed,
        client = "client-123",
        scopes = setOf("contact:write", "health:read"),
        expiresAt = Instant.now().plusSeconds(3600)
      )
    )

    val req = MockHttpServletRequest("POST", "/contact").apply {
      addHeader(ApiKeyAuthFilter.API_KEY_HEADER, rawKey)
    }
    val res = MockHttpServletResponse()

    filter.doFilter(req, res, chain)

    verify(chain).doFilter(any(), any())
    val auth = SecurityContextHolder.getContext().authentication
    assertEquals("client-123", auth?.name)

    val authorities = auth?.authorities?.map { it.authority }?.toSet()
    assertEquals(setOf("SCOPE_contact:write", "SCOPE_health:read"), authorities)
  }
}
