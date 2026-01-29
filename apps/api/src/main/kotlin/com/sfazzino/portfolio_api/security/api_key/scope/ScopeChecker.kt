package com.sfazzino.portfolio_api.security.api_key.scope

object ScopeChecker {
  fun hasScope(granted: Set<String>, required: String): Boolean {
    if (required in granted) return true
    if ("*" in granted) return true

    // "admin:*" is treated as superuser
    if ("admin:*" in granted) return true

    // "contact:*" covers "contact:write"
    val prefix = required.substringBefore(":")
    return "$prefix:*" in granted
  }
}
