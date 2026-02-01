package com.sfazzino.portfolio_api.security.rate_limiter

import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ClientIpResolverTest {

  @Test
  fun `uses X-Forwarded-For when present`() {
    val request = mock<HttpServletRequest> {
      on { getHeader("X-Forwarded-For") } doReturn "1.2.3.4"
      on { remoteAddr } doReturn "9.9.9.9"
    }

    val ip = ClientIpResolver.resolveIp(request)

    assertEquals("1.2.3.4", ip)
  }

  @Test
  fun `uses first IP when X-Forwarded-For contains multiple`() {
    val request = mock<HttpServletRequest> {
      on { getHeader("X-Forwarded-For") } doReturn "1.2.3.4, 5.6.7.8"
      on { remoteAddr } doReturn "9.9.9.9"
    }

    val ip = ClientIpResolver.resolveIp(request)

    assertEquals("1.2.3.4", ip)
  }

  @Test
  fun `trims whitespace in X-Forwarded-For`() {
    val request = mock<HttpServletRequest> {
      on { getHeader("X-Forwarded-For") } doReturn "  1.2.3.4  , 5.6.7.8 "
      on { remoteAddr } doReturn "9.9.9.9"
    }

    val ip = ClientIpResolver.resolveIp(request)

    assertEquals("1.2.3.4", ip)
  }

  @Test
  fun `falls back to remoteAddr when X-Forwarded-For missing`() {
    val request = mock<HttpServletRequest> {
      on { getHeader("X-Forwarded-For") } doReturn null
      on { remoteAddr } doReturn "9.9.9.9"
    }

    val ip = ClientIpResolver.resolveIp(request)

    assertEquals("9.9.9.9", ip)
  }

  @Test
  fun `returns unknown when both header and remoteAddr missing`() {
    val request = mock<HttpServletRequest> {
      on { getHeader("X-Forwarded-For") } doReturn null
      on { remoteAddr } doReturn null
    }

    val ip = ClientIpResolver.resolveIp(request)

    assertEquals("unknown", ip)
  }
}
