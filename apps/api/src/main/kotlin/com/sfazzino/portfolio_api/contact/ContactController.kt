package com.sfazzino.portfolio_api.contact

import com.sfazzino.portfolio_api.security.rate_limiter.ClientIpResolver.resolveIp
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contact")
class ContactController(
  private val contactService: ContactService
) {

  @PostMapping
  fun submit(httpRequest: HttpServletRequest, @Valid @RequestBody request: ContactRequest
  ): ResponseEntity<ContactResponse> {
    log.info("Received a new contact request $request from ip ${resolveIp(httpRequest)}")

    contactService.handle(request)
    return ResponseEntity.ok(ContactResponse(ok = true))
  }

  companion object {
    private val log = LoggerFactory.getLogger(ContactController::class.java)
  }
}
