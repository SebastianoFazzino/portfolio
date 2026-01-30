package com.sfazzino.portfolio_api.common

import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
@EnableJpaAuditing
class SpringSecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val auditor = SecurityContextHolder.getContext().authentication?.name ?: "system"
        return Optional.of(auditor)
    }
}