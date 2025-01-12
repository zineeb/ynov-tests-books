package com.example.tp_library

import com.example.tp_library.domain.model.Book
import com.example.tp_library.infrastructure.driven.postgres.BookDAO
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookDAOTest(
    @Autowired val bookDAO: BookDAO,
    @Autowired val jdbcTemplate: NamedParameterJdbcTemplate
) {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { "jdbc:tc:postgresql:15.3-alpine:///testdb" }
            registry.add("spring.datasource.username") { "testuser" }
            registry.add("spring.datasource.password") { "testpass" }
            registry.add("spring.datasource.driver-class-name") { "org.testcontainers.jdbc.ContainerDatabaseDriver" }
            registry.add("spring.liquibase.enabled") { "false" }
        }
    }

    @BeforeAll
    fun setupSchema() {
        jdbcTemplate.update(
            """
            CREATE TABLE IF NOT EXISTS book (
                id SERIAL PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL
            )
            """.trimIndent(), emptyMap<String, Any>()
        )
    }

    @BeforeEach
    fun cleanDB() {
        jdbcTemplate.update("DELETE FROM book", emptyMap<String, Any>())
    }

    @Test
    fun `findAll should return empty at start`() {
        val all = bookDAO.findAll()
        assertTrue(all.isEmpty(), "Expected empty list")
    }

    @Test
    fun `save then findAll`() {
        bookDAO.save(Book("Clean Code", "Robert Martin"))
        bookDAO.save(Book("Refactoring", "Martin Fowler"))

        val all = bookDAO.findAll()
        assertEquals(2, all.size)
        assertEquals("Clean Code", all.first().title)
    }
}
