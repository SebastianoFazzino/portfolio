package com.sfazzino.portfolio_api.security.cors

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig(
  private val props: CorsProperties
) : WebMvcConfigurer {

  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
      .allowedOrigins(*props.allowedOrigins.toTypedArray())
      .allowedMethods("POST", "GET", "OPTIONS")
      .allowedHeaders("Content-Type", "X-API-KEY")
      .allowCredentials(false)
      .maxAge(3600)
  }
}
