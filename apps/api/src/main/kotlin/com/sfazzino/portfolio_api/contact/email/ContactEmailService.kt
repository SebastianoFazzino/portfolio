package com.sfazzino.portfolio_api.contact.email

import com.sfazzino.portfolio_api.exception.ApplicationException.Companion.internalServerError
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class ContactEmailService(
  private val props: BrevoConfig
) {
  private val client = RestClient.create("https://api.brevo.com")

  fun send(name: String, email: String, message: String, ip: String): Boolean {
    if (props.apiKey.isBlank() || props.toEmail.isBlank() || props.fromEmail.isBlank()) {
      throw internalServerError("Email service is not properly configured")
    }

    val payload = mapOf(
      "sender" to mapOf("name" to props.fromName, "email" to props.fromEmail),
      "to" to listOf(mapOf("email" to props.toEmail, "name" to props.fromName)),
      "subject" to "Portfolio contact: $name",
      "textContent" to buildString {
        appendLine("Name: $name")
        appendLine("Email: $email")
        appendLine("IP: $ip")
        appendLine()
        append(message)
      },
      "replyTo" to mapOf("email" to email, "name" to name),
    )

    return try {
      val res = client.post()
        .uri("/v3/smtp/email")
        .header("api-key", props.apiKey)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .retrieve()
        .toBodilessEntity()

      res.statusCode.is2xxSuccessful
    } catch (e: Exception) {
      log.error("Failed to send contact email via Brevo", e)
      false
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(ContactEmailService::class.java)
  }
}
