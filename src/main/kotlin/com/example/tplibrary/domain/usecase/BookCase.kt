package com.example.tplibrary.domain.usecase

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository

/**
 * Use case for adding, listing, and reserving books.
 * Throws [BookDomainException] if business rules are violated.
 */
class BookCase(private val bookRepository: BookRepository) {

    /**
     * Adds a book to the repository if the author is not blacklisted.
     */
    fun addBook(book: Book) {
        if (book.author == "BadAuthor") {
            throw BookDomainException("Author is blacklisted in domain logic")
        }
        bookRepository.save(book)
    }

    /**
     * Returns all books sorted by title.
     */
    fun listBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }

    /**
     * Reserves a book identified by its title.
     * Throws [BookDomainException] if:
     *   - Book not found
     *   - Book is already reserved
     */
    fun reserveBook(title: String) {
        val existingBook = bookRepository.findByTitle(title)
        println("reserveBook() -> existingBook = $existingBook")
        if (existingBook == null) {
            throw BookDomainException("Book with title '$title' not found")
        }

        if (existingBook.reserved) {
            throw BookDomainException("Book is already reserved")
        }

        println("** Appel repository.update() avec le livre non réservé")
        bookRepository.update(existingBook.copy(reserved = true))
    }
}

/**
 * Exception thrown when a domain/business rule is violated.
 */
class BookDomainException(message: String) : RuntimeException(message)
