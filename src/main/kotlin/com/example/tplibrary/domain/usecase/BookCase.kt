package com.example.tplibrary.domain.usecase

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository

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
