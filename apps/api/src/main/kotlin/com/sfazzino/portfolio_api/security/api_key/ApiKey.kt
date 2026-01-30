package com.sfazzino.portfolio_api.security.api_key

import com.sfazzino.portfolio_api.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "api_keys", schema = "portfolio")
class ApiKey(
    val key: String,
    val client: String,
    val scopes: Set<String>,
    val expiresAt: Instant? = null
): BaseEntity() {

    fun isValid(): Boolean {
        return expiresAt == null || expiresAt!!.isAfter(Instant.now())
    }
}