package com.sfazzino.portfolio_api.security.api_key

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class ApiKeyHasherTest {

  @Test
  fun `same input produces same hash`() {
    val key = "dev-test-key"

    val h1 = ApiKeyHasher.hash(key)
    val h2 = ApiKeyHasher.hash(key)

    assertEquals(h1, h2)
  }

  @Test
  fun `different inputs produce different hashes`() {
    val h1 = ApiKeyHasher.hash("key-one")
    val h2 = ApiKeyHasher.hash("key-two")

    assertNotEquals(h1, h2)
  }

  @Test
  fun `hash is valid sha256 hex`() {
    val hash = ApiKeyHasher.hash("test")

    // SHA-256 = 64 hex chars
    assertEquals(64, hash.length)
    assert(hash.matches(Regex("[0-9a-f]{64}")))
  }

  @Test
  fun `hash matches known sha256 value`() {
    val hash = ApiKeyHasher.hash("test")

    // precomputed SHA-256("test")
    assertEquals(
      "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
      hash
    )
  }
}
