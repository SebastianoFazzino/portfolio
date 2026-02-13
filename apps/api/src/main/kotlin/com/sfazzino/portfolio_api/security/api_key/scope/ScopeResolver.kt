package com.sfazzino.portfolio_api.security.api_key.scope

import jakarta.servlet.http.HttpServletRequest

object ScopeResolver {
  const val ADMIN_ALL = "admin:*"
  const val CONTACT = "contact:*"
  const val PING = "ping"

  fun requiredScope(request: HttpServletRequest): String? {
    val path = request.requestURI.removePrefix(request.contextPath ?: "")
    val method = request.method.uppercase()

    // public
    if (method == "OPTIONS") return null
    if (method == "GET" && path == "/healthz") return null

    // ping (frontend heartbeat)
    if (method == "POST" && path == "/ping") return PING

    // contact
    if (method == "POST" && path == "/contact" ) return CONTACT
    if (method == "GET" && path == "/knowledge/ask") return CONTACT
    if (method == "GET" && path == "/knowledge/ask/stream") return CONTACT

    // default: protect everything else
    return ADMIN_ALL
  }
}
