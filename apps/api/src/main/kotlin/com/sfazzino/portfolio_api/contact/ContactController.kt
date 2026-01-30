package com.sfazzino.portfolio_api.contact

import jakarta.validation.Valid
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
  fun submit(@Valid @RequestBody request: ContactRequest): ResponseEntity<ContactResponse> {
    contactService.handle(request)
    return ResponseEntity.ok(ContactResponse(ok = true))
  }
}
