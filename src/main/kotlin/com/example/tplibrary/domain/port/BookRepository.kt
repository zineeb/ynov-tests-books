package com.example.tplibrary.domain.port

import com.example.tplibrary.domain.model.Book

/**
 * Port that defines how the domain interacts with a Book repository.
 */
interface BookRepository {
    fun save(book: Book)
    fun findAll(): List<Book>
    fun findByTitle(title: String): Book?
    fun update(book: Book)
}
