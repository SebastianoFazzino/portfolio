package com.sfazzino.portfolio_api.rag

import com.sfazzino.portfolio_api.TestcontainersConfiguration
import com.sfazzino.portfolio_api.rag.repository.KnowledgeChunkRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate

@JdbcTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KnowledgeChunkRepositoryTest {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    lateinit var repository: KnowledgeChunkRepository

    @BeforeEach
    fun setUp() {
        repository = KnowledgeChunkRepository(jdbcTemplate)

        jdbcTemplate.execute(
            """
            CREATE SCHEMA IF NOT EXISTS portfolio;
            
            CREATE EXTENSION IF NOT EXISTS vector;

            CREATE TABLE IF NOT EXISTS portfolio.knowledge_chunks (
                id uuid PRIMARY KEY,
                source text NOT NULL,
                content text NOT NULL,
                content_hash text NOT NULL,
                embedding portfolio.vector(384) NOT NULL,
                created_by text,
                created_at timestamptz NOT NULL DEFAULT now(),
                last_modified_by text,
                last_modified_at timestamptz
            );
            """.trimIndent()
        )
    }

    @Test
    fun `save inserts row and exists queries return true`() {
        val id = repository.save(
            source = "profile",
            content = "hello world",
            contentHash = "hash123",
            embedding = embedding()
        )

        assertNotNull(id)
        assertTrue(repository.existsBySource("profile"))
        assertTrue(repository.existsByContentHash("hash123"))
    }

    @Test
    fun `findAll returns dto list`() {
        repository.save(
            source = "profile",
            content = "content 1",
            contentHash = "hash1",
            embedding = embedding()
        )

        repository.save(
            source = "profile",
            content = "content 2",
            contentHash = "hash2",
            embedding = embedding()
        )

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertEquals("profile", result.first().source)
        assertNotNull(result.first().createdAt)
    }

    @Test
    fun `deleteBySource deletes all rows for source`() {
        repository.save(
            source = "profile",
            content = "content",
            contentHash = "hash",
            embedding = embedding()
        )

        val deleted = repository.deleteBySource("profile")

        assertEquals(1, deleted)
        assertFalse(repository.existsBySource("profile"))
    }

    @Test
    fun `findTopSimilar returns results`() {
        repository.save(
            source = "profile",
            content = "similar content",
            contentHash = "hash",
            embedding = embedding()
        )

        val results = repository.findTopSimilar(
            queryEmbedding = embedding(),
            limit = 5
        )

        assertEquals(1, results.size)
        assertEquals("profile", results.first().source)
    }

private fun embedding(): FloatArray =
        FloatArray(384) { 0.01f }
}