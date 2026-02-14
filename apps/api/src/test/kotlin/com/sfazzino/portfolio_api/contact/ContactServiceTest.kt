package com.sfazzino.portfolio_api.contact

import com.sfazzino.portfolio_api.contact.email.ContactEmailService
import com.sfazzino.portfolio_api.contact.moderation.ModerationDecision
import com.sfazzino.portfolio_api.contact.moderation.ModerationService
import com.sfazzino.portfolio_api.exception.ApplicationException
import com.sfazzino.portfolio_api.security.rate_limiter.IpRateLimiter
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class ContactServiceTest {

  private val request: HttpServletRequest = mock {
    on { getHeader(any()) } doReturn null
    on { remoteAddr } doReturn "1.2.3.4"
  }

  private val rateLimiter: IpRateLimiter = mock()
  private val moderationService: ModerationService = mock()
  private val emailService: ContactEmailService = mock()

  private val service = ContactService(
      request = request,
      rateLimiter = rateLimiter,
      moderationService = moderationService,
      emailService = emailService,
      moderationEnabled = true
  )

  @Test
  fun `honeypot - website filled - returns OK and does nothing`() {
    val dto = ContactRequest(
      name = "  Seb  ",
      email = "  seb@example.com  ",
      message = "  hello  ",
      website = "https://bot.example"
    )

    assertDoesNotThrow { service.handle(dto) }

    verifyNoInteractions(rateLimiter, moderationService, emailService)
  }

  @Test
  fun `rate limited - throws tooManyRequests - no moderation, no email`() {
    whenever(rateLimiter.allow("1.2.3.4")).thenReturn(false)

    val dto = ContactRequest(
      name = " Seb ",
      email = " seb@example.com ",
      message = " hello ",
      website = null
    )

    assertThrows(ApplicationException::class.java) {
      service.handle(dto)
    }

    verify(rateLimiter).allow("1.2.3.4")
    verifyNoInteractions(moderationService, emailService)
  }

  @Test
  fun `moderation blocks - returns OK and does not send email`() {
    whenever(rateLimiter.allow("1.2.3.4")).thenReturn(true)
    whenever(moderationService.check(message = "SELECT * FROM users")).thenReturn(
      ModerationDecision.block("injection")
    )

    val dto = ContactRequest(
      name = " Seb ",
      email = " seb@example.com ",
      message = "SELECT * FROM users",
      website = null
    )

    assertThrows(ApplicationException::class.java) { service.handle(dto) }

    verify(rateLimiter).allow("1.2.3.4")
    verify(moderationService).check(message = "SELECT * FROM users")
    verify(emailService, never()).send(any(), any(), any(), any())
  }

  @Test
  fun `email send fails - throws emailSendFailed`() {
    whenever(rateLimiter.allow("1.2.3.4")).thenReturn(true)
    whenever(moderationService.check(message = "hello")).thenReturn(
      ModerationDecision.allow()
    )
    whenever(
      emailService.send(
        name = "Seb",
        email = "seb@example.com",
        message = "hello",
        ip = "1.2.3.4"
      )
    ).thenReturn(false)

    val dto = ContactRequest(
      name = "  Seb  ",
      email = "  seb@example.com  ",
      message = "  hello  ",
      website = null
    )

    assertThrows(ApplicationException::class.java) {
      service.handle(dto)
    }

    verify(rateLimiter).allow("1.2.3.4")
    verify(moderationService).check(message = "hello")
    verify(emailService).send(
      name = "Seb",
      email = "seb@example.com",
      message = "hello",
      ip = "1.2.3.4"
    )
  }

  @Test
  fun `happy path - trims fields, checks moderation, sends email`() {
    whenever(rateLimiter.allow("1.2.3.4")).thenReturn(true)
    whenever(moderationService.check(message = "hello")).thenReturn(
      ModerationDecision.allow()
    )
    whenever(
      emailService.send(
        name = "Seb",
        email = "seb@example.com",
        message = "hello",
        ip = "1.2.3.4"
      )
    ).thenReturn(true)

    val dto = ContactRequest(
      name = "  Seb  ",
      email = "  seb@example.com  ",
      message = "  hello  ",
      website = null
    )

    assertDoesNotThrow { service.handle(dto) }

    verify(rateLimiter).allow("1.2.3.4")
    verify(moderationService).check(message = "hello")
    verify(emailService).send(
      name = "Seb",
      email = "seb@example.com",
      message = "hello",
      ip = "1.2.3.4"
    )
  }
}
