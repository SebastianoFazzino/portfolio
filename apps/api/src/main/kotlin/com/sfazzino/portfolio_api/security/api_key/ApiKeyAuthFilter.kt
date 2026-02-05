package com.sfazzino.portfolio_api.security.api_key

import com.sfazzino.portfolio_api.exception.ErrorCodes.EXPIRED_API_KEY
import com.sfazzino.portfolio_api.exception.ErrorCodes.INVALID_API_KEY
import com.sfazzino.portfolio_api.exception.ErrorCodes.MISSING_API_KEY
import com.sfazzino.portfolio_api.security.api_key.scope.ScopeChecker
import com.sfazzino.portfolio_api.security.api_key.scope.ScopeResolver
import com.sfazzino.portfolio_api.security.crypto.CryptoUtil.hash
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthFilter(
    private val apiKeyRepository: ApiKeyRepository
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val method = request.method
        val path = request.requestURI.removePrefix(request.contextPath ?: "")
        return method == "OPTIONS" || (method == "GET" && path == "/healthz")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requiredScope = ScopeResolver.requiredScope(request)
        if (requiredScope == null) {
            filterChain.doFilter(request, response)
            return
        }

        val rawKey = request.getHeader(API_KEY_HEADER)?.trim()
        if (rawKey.isNullOrEmpty()) {
            unauthorized(response, MISSING_API_KEY)
            return
        }

        val apiKey = apiKeyRepository.findByKey(hash(rawKey))
            ?: run {
                unauthorized(response, INVALID_API_KEY)
                return
            }

        if (!apiKey.isValid()) {
            unauthorized(response, EXPIRED_API_KEY)
            return
        }

        if (!ScopeChecker.hasScope(apiKey.scopes, requiredScope)) {
            forbidden(response, requiredScope)
            return
        }

        val authorities = apiKey.scopes.map {
            SimpleGrantedAuthority("$SCOPE_PREFIX$it")
        }

        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(apiKey.client, null, authorities)

        filterChain.doFilter(request, response)
    }

    private fun unauthorized(res: HttpServletResponse, code: String) {
        res.status = 401
        res.contentType = "application/json"
        res.writer.write("""{"error":"unauthorized","code":"$code"}""")
    }

    private fun forbidden(res: HttpServletResponse, required: String) {
        res.status = 403
        res.contentType = "application/json"
        res.writer.write(
            """{"error":"forbidden","required_scope":"$required"}"""
        )
    }

    companion object {
        const val API_KEY_HEADER = "X-API-KEY"
        const val SCOPE_PREFIX = "SCOPE_"
    }
}
