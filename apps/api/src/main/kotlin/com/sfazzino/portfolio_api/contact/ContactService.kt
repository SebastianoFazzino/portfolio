package com.sfazzino.portfolio_api.contact

import com.sfazzino.portfolio_api.contact.email.ContactEmailService
import com.sfazzino.portfolio_api.contact.moderation.ModerationService
import com.sfazzino.portfolio_api.exception.ApplicationException
import com.sfazzino.portfolio_api.security.rate_limiter.ClientIpResolver
import com.sfazzino.portfolio_api.security.rate_limiter.IpRateLimiter
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ContactService(
    private val request: HttpServletRequest,
    private val rateLimiter: IpRateLimiter,
    private val moderationService: ModerationService,
    private val emailService: ContactEmailService,
) {

  fun handle(dto: ContactRequest) {
    // Honeypot: pretend OK, do nothing
    if (!dto.website.isNullOrBlank()) return

    val name = dto.name.trim()
    val email = dto.email.trim()
    val message = dto.message.trim()

    val ip = ClientIpResolver.resolveIp(request)

    if (!rateLimiter.allow(ip)) {
      throw ApplicationException.tooManyRequests()
    }

    val decision = moderationService.check(message = message)
    if (!decision.allowed) {
      throw ApplicationException.contactRejected(message = decision.reason)
    }

    val ok = emailService.send(name = name, email = email, message = message, ip = ip)
    if (!ok) throw ApplicationException.emailSendFailed()

    log.info("Email sent from contact form: ip=$ip, name=$name, email=$email")
  }

  companion object {
    private val log = LoggerFactory.getLogger(ContactService::class.java)
  }
}
