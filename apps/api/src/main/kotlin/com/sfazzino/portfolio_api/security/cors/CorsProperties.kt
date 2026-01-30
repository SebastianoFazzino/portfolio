package com.sfazzino.portfolio_api.security.cors

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
  var allowedOrigins: List<String> = emptyList()
)
