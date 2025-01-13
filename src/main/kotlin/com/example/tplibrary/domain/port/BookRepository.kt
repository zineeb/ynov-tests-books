package com.example.tplibrary.domain.port

import com.example.tplibrary.domain.model.Book

interface BookRepository {
    fun save(book: Book)
    fun findAll(): List<Book>
}
