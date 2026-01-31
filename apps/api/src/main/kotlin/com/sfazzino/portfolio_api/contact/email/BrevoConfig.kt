package com.sfazzino.portfolio_api.contact.email

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "contact.brevo")
data class BrevoConfig(
    val apiKey: String,
    val toEmail: String,
    val fromEmail: String,
    val fromName: String
)