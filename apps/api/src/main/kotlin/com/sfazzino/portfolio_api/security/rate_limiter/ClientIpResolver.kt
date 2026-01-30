package com.sfazzino.portfolio_api.security.rate_limiter

import jakarta.servlet.http.HttpServletRequest

object ClientIpResolver {
  fun resolve(request: HttpServletRequest): String {
    val xff = request.getHeader("X-Forwarded-For")
    if (!xff.isNullOrBlank()) return xff.split(",")[0].trim()
    return request.remoteAddr ?: "unknown"
  }
}
