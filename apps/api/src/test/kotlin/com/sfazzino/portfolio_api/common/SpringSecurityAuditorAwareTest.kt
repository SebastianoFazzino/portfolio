package com.sfazzino.portfolio_api.common

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class SpringSecurityAuditorAwareTest {

  private val auditorAware = SpringSecurityAuditorAware()

  @AfterEach
  fun cleanup() {
    SecurityContextHolder.clearContext()
  }

  @Test
  fun `returns authenticated username when security context is set`() {
    SecurityContextHolder.getContext().authentication =
      UsernamePasswordAuthenticationToken("seb", null, emptyList())

    val auditor = auditorAware.currentAuditor.orElse(null)

    assertEquals("seb", auditor)
  }

  @Test
  fun `returns system when no authentication is present`() {
    SecurityContextHolder.clearContext()

    val auditor = auditorAware.currentAuditor.orElse(null)

    assertEquals("system", auditor)
  }
}
