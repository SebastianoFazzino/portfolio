package com.sfazzino.portfolio_api.common

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    @field:Column(updatable = false, nullable = false)
    var id: UUID? = null

    @field:CreatedBy
    var createdBy: String? = null

    @field:CreatedDate
    @field:Column(updatable = false)
    var createdAt: Instant? = null

    @field:LastModifiedBy
    var lastModifiedBy: String? = null

    @field:LastModifiedDate
    var lastModifiedAt: Instant? = null
}