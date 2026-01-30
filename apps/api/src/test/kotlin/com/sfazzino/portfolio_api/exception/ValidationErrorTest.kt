package com.sfazzino.portfolio_api.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidationErrorTest {

  @Test
  fun `fromArgs creates one entry with unique messages`() {
    val errors = ValidationError.fromArgs("email", "invalid", "invalid", "required")

    assertEquals(1, errors.size)
    assertEquals("email", errors[0].field)
    assertEquals(setOf("invalid", "required"), errors[0].messages)
  }

  @Test
  fun `fromMap converts lists to unique message sets`() {
    val errors = ValidationError.fromMap(
      mapOf(
        "name" to listOf("required", "required"),
        "email" to listOf("invalid")
      )
    )

    val byField = errors.associateBy { it.field }
    assertEquals(setOf("required"), byField["name"]!!.messages)
    assertEquals(setOf("invalid"), byField["email"]!!.messages)
  }
}
