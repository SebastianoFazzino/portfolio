package com.sfazzino.portfolio_api.rag.repository

import com.sfazzino.portfolio_api.rag.dtos.KnowledgeChunkDto
import com.sfazzino.portfolio_api.rag.dtos.KnowledgeChunkRow
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Uses JdbcTemplate for better compatibility with Postgres vector operations.
 */
@Repository
class KnowledgeChunkRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun existsBySource(source: String): Boolean {
        return jdbcTemplate.queryForObject<Boolean>(
            "SELECT EXISTS (SELECT 1 FROM portfolio.knowledge_chunks WHERE source = ?)",
            source
        ) ?: false
    }

    fun existsByContentHash(contentHash: String): Boolean {
        return jdbcTemplate.queryForObject<Boolean>(
            "SELECT EXISTS (SELECT 1 FROM portfolio.knowledge_chunks WHERE content_hash = ?)",
            contentHash
        ) ?: false
    }

    fun save(
        source: String,
        content: String,
        contentHash: String,
        embedding: FloatArray,
    ): UUID {
        val id = UUID.randomUUID()
        val createdBy = getCurrentAuditor()
        val vectorLiteral = embedding.joinToString(prefix = "[", postfix = "]", separator = ",")

        jdbcTemplate.update(
            """
                INSERT INTO portfolio.knowledge_chunks (
                    id, source, content, content_hash, embedding, created_by
                )
                VALUES (?, ?, ?, ?, ?::portfolio.vector(384), ?)
            """.trimIndent(),
            id,
            source,
            content,
            contentHash,
            vectorLiteral,
            createdBy
        )

        return id
    }

    @Transactional
    fun deleteBySource(source: String): Int {
        return jdbcTemplate.update(
            "DELETE FROM portfolio.knowledge_chunks WHERE source = ?",
            source
        )
    }

    fun findTopSimilar(queryEmbedding: FloatArray, limit: Int): List<KnowledgeChunkRow> {
        val vectorLiteral = queryEmbedding.joinToString(prefix = "[", postfix = "]", separator = ",")

        return jdbcTemplate.query(
            """
                    SELECT id, source, content, content_hash
                    FROM portfolio.knowledge_chunks
                    ORDER BY embedding OPERATOR(portfolio.<=>) ?::portfolio.vector(384)
                    LIMIT ?
                """.trimIndent(),
            { resultSet, _ ->
                KnowledgeChunkRow(
                    id = resultSet.getObject("id", UUID::class.java),
                    source = resultSet.getString("source"),
                    content = resultSet.getString("content"),
                    contentHash = resultSet.getString("content_hash")
                )
            },
            vectorLiteral,
            limit
        )
    }

    fun findAll(): List<KnowledgeChunkDto> {
        return jdbcTemplate.query(
            """
            SELECT
                id,
                source,
                content,
                content_hash,
                created_by,
                created_at,
                last_modified_by,
                last_modified_at
            FROM portfolio.knowledge_chunks
            ORDER BY created_at DESC
            """.trimIndent()
        ) { rs, _ ->
            KnowledgeChunkDto(
                id = rs.getObject("id", UUID::class.java),
                source = rs.getString("source"),
                content = rs.getString("content"),
                contentHash = rs.getString("content_hash"),
                createdBy = rs.getString("created_by"),
                createdAt = rs.getTimestamp("created_at").toInstant(),
                lastModifiedBy = rs.getString("last_modified_by"),
                lastModifiedAt = rs.getTimestamp("last_modified_at")?.toInstant()
            )
        }
    }

    private fun getCurrentAuditor(): String {
        return SecurityContextHolder.getContext().authentication?.name ?: "system"
    }
}
