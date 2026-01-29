package com.sfazzino.portfolio_api.security.api_key

import java.security.MessageDigest

object ApiKeyHasher {

    fun hash(rawKey: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(rawKey.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
