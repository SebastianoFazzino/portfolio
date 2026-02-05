package com.sfazzino.portfolio_api.security.api_key.bootstrap

import com.sfazzino.portfolio_api.security.api_key.ApiKey
import com.sfazzino.portfolio_api.security.api_key.ApiKeyHasher.hash
import com.sfazzino.portfolio_api.security.api_key.ApiKeyRepository
import com.sfazzino.portfolio_api.security.api_key.scope.ScopeResolver.ADMIN_ALL
import com.sfazzino.portfolio_api.security.api_key.scope.ScopeResolver.CONTACT
import com.sfazzino.portfolio_api.security.api_key.scope.ScopeResolver.PING
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("!test")
@Component
class InitialApiKeysSetup(
    private val apiKeyRepository: ApiKeyRepository,
    @param:Value($$"${security.bootstrap.api-keys.admin:}") private val adminKeyRaw: String,
    @param:Value($$"${security.bootstrap.api-keys.client:}") private val clientKeyRaw: String,
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {
        if (apiKeyRepository.count() > 0L) {
            log.info("API keys already present, skipping bootstrap")
            return
        }

        if (adminKeyRaw.isBlank() || clientKeyRaw.isBlank()) {
            log.warn("Bootstrap API keys missing, skipping bootstrap.")
            return
        }

        log.info("Bootstrapping initial API keys (admin + web client)")
        apiKeyRepository.save(
            ApiKey(
                key = hash(adminKeyRaw.trim()),
                client = CLIENT_ADMIN,
                scopes = setOf(ADMIN_ALL),
                expiresAt = null
            )
        )

        apiKeyRepository.save(
            ApiKey(
                key = hash(clientKeyRaw.trim()),
                client = CLIENT_WEB,
                scopes = setOf(CONTACT, PING),
                expiresAt = null
            )
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(InitialApiKeysSetup::class.java)
        private const val CLIENT_ADMIN = "admin"
        private const val CLIENT_WEB = "web"
    }
}
