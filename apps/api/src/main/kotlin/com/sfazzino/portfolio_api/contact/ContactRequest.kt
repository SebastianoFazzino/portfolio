package com.sfazzino.portfolio_api.contact

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ContactRequest(
    @field:NotBlank(message = "name is required")
    @field:Size(max = 120, message = "name is too long")
    val name: String,

    @field:NotBlank(message = "email is required")
    @field:Email(message = "email is invalid")
    @field:Size(max = 254, message = "email is too long")
    val email: String,

    @field:NotBlank(message = "message is required")
    @field:Size(max = 4000, message = "message is too long")
    val message: String,

    // honeypot
    @field:Size(max = 200, message = "website is too long")
    val website: String? = null,
)
