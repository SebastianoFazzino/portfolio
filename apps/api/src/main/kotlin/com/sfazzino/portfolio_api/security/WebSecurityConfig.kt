package com.sfazzino.portfolio_api.security

import com.sfazzino.portfolio_api.security.api_key.ApiKeyAuthFilter
import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val apiKeyAuthFilter: ApiKeyAuthFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .formLogin { it.disable() }
            .httpBasic { it.disable() }

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/healthz").permitAll()
                    .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                    .anyRequest().authenticated()
            }

            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}