package com.sfazzino.portfolio_api.knowledge

import com.sfazzino.portfolio_api.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Array

@Entity
@Suppress("ArrayInDataClass")
@Table(name = "knowledge_chunks", schema = "portfolio")
data class KnowledgeChunk(

    val source: String,

    val content: String,

    val contentHash: String,

    @Array(length = 384)
    val embedding: FloatArray
): BaseEntity()