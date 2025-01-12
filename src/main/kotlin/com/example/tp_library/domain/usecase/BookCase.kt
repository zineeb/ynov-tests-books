package com.example.tp_library.domain.usecase

import com.example.tp_library.domain.model.Book
import com.example.tp_library.domain.port.BookRepository

class BookCase(private val bookRepository: BookRepository) {
    fun addBook(book: Book) {
        if (book.author == "BadAuthor") {
            throw BookDomainException("Author is blacklisted in domain logic")
        }
        bookRepository.save(book)
    }

    fun listBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }
}