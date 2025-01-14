package com.example.tplibrary.infrastructure.driven.postgres

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
open class BookDAO(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : BookRepository {

    override fun save(book: Book) {
        val sql = """
            INSERT INTO book (title, author)
            VALUES (:title, :author)
        """.trimIndent()
        val params = mapOf("title" to book.title, "author" to book.author)
        jdbcTemplate.update(sql, params)
    }

    override fun findAll(): List<Book> {
        val sql = "SELECT title, author FROM book ORDER BY title"
        val paramMap: Map<String, Any> = emptyMap()

        return jdbcTemplate.query(sql, paramMap) { rs, _ ->
            Book(rs.getString("title"), rs.getString("author"))
        }
    }
}
