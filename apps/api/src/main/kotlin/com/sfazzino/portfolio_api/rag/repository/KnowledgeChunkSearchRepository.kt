package com.sfazzino.portfolio_api.rag.repository

import com.sfazzino.portfolio_api.rag.dtos.KnowledgeChunkRow
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class KnowledgeChunkSearchRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun findTopSimilar(queryEmbedding: FloatArray, limit: Int): List<KnowledgeChunkRow> {
        val vectorLiteral = queryEmbedding.joinToString(prefix = "[", postfix = "]", separator = ",")

        return jdbcTemplate.query(
            """
                    SELECT id, source, content
                    FROM portfolio.knowledge_chunks
                    ORDER BY embedding OPERATOR(portfolio.<=>) ?::portfolio.vector(768)
                    LIMIT ?
                """.trimIndent(),
            { resultSet, _ ->
                KnowledgeChunkRow(
                    id = resultSet.getObject("id", UUID::class.java),
                    source = resultSet.getString("source"),
                    content = resultSet.getString("content")
                )
            },
            vectorLiteral,
            limit
        )
    }
}
