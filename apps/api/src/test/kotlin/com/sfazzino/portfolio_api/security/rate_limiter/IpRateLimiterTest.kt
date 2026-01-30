package com.sfazzino.portfolio_api.security.rate_limiter

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IpRateLimiterTest {

  private fun limiter(
    windowMs: Long = 100,
    maxRequests: Int = 3
  ): IpRateLimiter {
    val props = ContactRateLimitProps(
      windowMs = windowMs,
      maxRequests = maxRequests
    )
    return IpRateLimiter(props)
  }

  @Test
  fun `first request for ip is allowed`() {
    val limiter = limiter()

    assertTrue(limiter.allow("1.2.3.4"))
  }

  @Test
  fun `allows up to maxRequests within window`() {
    val limiter = limiter(maxRequests = 3)

    assertTrue(limiter.allow("1.2.3.4"))
    assertTrue(limiter.allow("1.2.3.4"))
    assertTrue(limiter.allow("1.2.3.4"))
  }

  @Test
  fun `blocks when maxRequests exceeded`() {
    val limiter = limiter(maxRequests = 2)

    assertTrue(limiter.allow("1.2.3.4"))
    assertTrue(limiter.allow("1.2.3.4"))
    assertFalse(limiter.allow("1.2.3.4"))
  }

  @Test
  fun `allows again after window resets`() {
    val limiter = limiter(windowMs = 50, maxRequests = 1)

    assertTrue(limiter.allow("1.2.3.4"))
    assertFalse(limiter.allow("1.2.3.4"))

    Thread.sleep(60)

    assertTrue(limiter.allow("1.2.3.4"))
  }

  @Test
  fun `different ips have independent limits`() {
    val limiter = limiter(maxRequests = 1)

    assertTrue(limiter.allow("1.2.3.4"))
    assertTrue(limiter.allow("5.6.7.8"))

    assertFalse(limiter.allow("1.2.3.4"))
    assertFalse(limiter.allow("5.6.7.8"))
  }

  @Test
  fun `counter increments correctly`() {
    val limiter = limiter(maxRequests = 3)

    assertTrue(limiter.allow("1.2.3.4")) // 1
    assertTrue(limiter.allow("1.2.3.4")) // 2
    assertTrue(limiter.allow("1.2.3.4")) // 3
    assertFalse(limiter.allow("1.2.3.4")) // 4 → blocked
  }
}
