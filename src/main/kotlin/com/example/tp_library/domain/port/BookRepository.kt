package com.example.tp_library.domain.port

import com.example.tp_library.domain.model.Book

interface BookRepository {
    fun save(book: Book)
    fun findAll(): List<Book>
}