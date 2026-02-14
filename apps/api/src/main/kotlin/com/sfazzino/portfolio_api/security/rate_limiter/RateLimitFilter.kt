package com.sfazzino.portfolio_api.security.rate_limiter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RateLimitFilter(
  private val rateLimiter: IpRateLimiter
) : OncePerRequestFilter() {

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val path = request.requestURI

    val isProtected =
      path.startsWith("/api/knowledge/ask/stream") || path.startsWith("/api/contact/")

    if (!isProtected) {
      filterChain.doFilter(request, response)
      return
    }

    val ip = ClientIpResolver.resolveIp(request)
    if (rateLimiter.allow(ip)) {
      filterChain.doFilter(request, response)
      return
    }

    response.status = 429

    val accept = request.getHeader("Accept") ?: ""
    val isSse = accept.contains("text/event-stream")

    if (!isSse) {
      response.contentType = "application/json"
      response.writer.write("""{"error":"too_many_requests"}""")
    }

    response.flushBuffer()
  }
}
