package com.example.tplibrary.infrastructure.driven.postgres

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

/**
 * A PostgreSQL-based repository implementation for Book.
 * Uses Spring's NamedParameterJdbcTemplate for queries.
 */
@Repository
open class BookDAO(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : BookRepository {

    override fun save(book: Book) {
        val sql = """
            INSERT INTO book (title, author, reserved)
            VALUES (:title, :author, :reserved)
        """.trimIndent()

        val params = mapOf(
            "title" to book.title,
            "author" to book.author,
            "reserved" to book.reserved
        )
        jdbcTemplate.update(sql, params)
    }

    override fun findAll(): List<Book> {
        val sql = "SELECT title, author, reserved FROM book ORDER BY title"
        return jdbcTemplate.query(sql, emptyMap<String, Any>()) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author"),
                reserved = rs.getBoolean("reserved")
            )
        }
    }

    override fun findByTitle(title: String): Book? {
        val sql = """
            SELECT title, author, reserved
              FROM book
             WHERE title = :title
        """.trimIndent()

        val paramMap = mapOf("title" to title)
        return jdbcTemplate.query(sql, paramMap) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author"),
                reserved = rs.getBoolean("reserved")
            )
        }.firstOrNull()
    }

    override fun update(book: Book) {
        val sql = """
            UPDATE book
               SET author = :author,
                   reserved = :reserved
             WHERE title = :title
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
            .addValue("reserved", book.reserved)

        jdbcTemplate.update(sql, params)
    }
}
