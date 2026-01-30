package com.sfazzino.portfolio_api.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class SecurityConfigs {

    @Bean
    fun userDetailsService(): UserDetailsService =
        InMemoryUserDetailsManager()
}
