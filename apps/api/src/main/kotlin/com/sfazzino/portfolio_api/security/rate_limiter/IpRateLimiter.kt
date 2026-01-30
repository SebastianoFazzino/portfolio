package com.sfazzino.portfolio_api.security.rate_limiter

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class IpRateLimiter(
  private val props: ContactRateLimitProps
) {
  private data class State(
    var count: Int,
    var resetAtMs: Long
  )

  private val states = ConcurrentHashMap<String, State>()

  fun allow(ip: String): Boolean {
    val now = System.currentTimeMillis()
    val existing = states[ip]

    // new window
    if (existing == null || now > existing.resetAtMs) {
      states[ip] = State(
        count = 1,
        resetAtMs = now + props.windowMs
      )
      return true
    }

    // limit reached
    if (existing.count >= props.maxRequests) {
      return false
    }

    existing.count += 1
    return true
  }
}