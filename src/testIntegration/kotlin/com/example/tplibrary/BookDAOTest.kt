package com.example.tplibrary

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.infrastructure.driven.postgres.BookDAO
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Integration tests for the PostgreSQL-backed BookDAO repository.
 * Utilizes Testcontainers to spin up a temporary PostgreSQL instance.
 */
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
            // Use Testcontainers for Postgres
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
                author VARCHAR(255) NOT NULL,
                reserved BOOLEAN NOT NULL DEFAULT FALSE
            )
            """.trimIndent(),
            emptyMap<String, Any>()
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

    @Test
    fun `reserve should update the reserved column`() {
        // Save a new book (reserved=false by default)
        val initialBook = Book("TestTitle", "TestAuthor", reserved = false)
        bookDAO.save(initialBook)

        // Check it was saved as non-reserved
        var found = bookDAO.findByTitle("TestTitle")
        assertNotNull(found)
        assertFalse(found!!.reserved)

        // Now update the book to be reserved
        val updatedBook = found.copy(reserved = true)
        bookDAO.update(updatedBook)

        found = bookDAO.findByTitle("TestTitle")
        assertNotNull(found)
        assertTrue(found!!.reserved, "Book should now be reserved")
    }
}
