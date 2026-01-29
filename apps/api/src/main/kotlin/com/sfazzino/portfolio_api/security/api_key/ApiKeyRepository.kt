package com.sfazzino.portfolio_api.security.api_key

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApiKeyRepository: JpaRepository<ApiKey, UUID> {
    fun findByKey(key: String): ApiKey?
}