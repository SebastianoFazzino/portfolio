package com.sfazzino.portfolio_api.rag

import com.sfazzino.portfolio_api.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Suppress("ArrayInDataClass")
@Table(name = "knowledge_chunks", schema = "portfolio")
data class KnowledgeChunk(

    val source: String,

    val content: String,

    val contentHash: String,

    @org.hibernate.annotations.Array(length = 768)
    val embedding: FloatArray
): BaseEntity()