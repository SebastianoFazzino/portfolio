package com.sfazzino.portfolio_api.security.rate_limiter

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "contact.rate-limit")
data class ContactRateLimitProps(
  var windowMs: Long = 60_000,
  var maxRequests: Int = 5,
)