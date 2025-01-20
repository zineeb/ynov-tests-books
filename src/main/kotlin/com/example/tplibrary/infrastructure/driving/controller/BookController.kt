package com.example.tplibrary.infrastructure.driving.controller

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.usecase.BookCase
import com.example.tplibrary.infrastructure.driving.controller.dto.BookDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for Book operations.
 * Provides endpoints to get books, add books, and reserve a book.
 */
@RestController
@RequestMapping("/books")
class BookController(private val bookCase: BookCase) {

    @GetMapping
    fun getBooks(): ResponseEntity<List<BookDTO>> {
        val books = bookCase.listBooks()
        // Return the 'reserved' status in the DTO
        val dtos = books.map { BookDTO(it.title, it.author, it.reserved) }
        return ResponseEntity.ok(dtos)
    }

    @PostMapping
    fun addBook(@RequestBody bookDTO: BookDTO): ResponseEntity<String> {
        // Simple validation
        if (bookDTO.title.isBlank()) {
            return ResponseEntity.badRequest().body("Le titre ne peut pas être vide")
        }
        val book = Book(bookDTO.title, bookDTO.author)
        bookCase.addBook(book)
        return ResponseEntity.ok("Book added successfully!")
    }

    /**
     * Reserves a book by its title.
     * Throws domain exception if the book is not found or already reserved.
     */
    @PostMapping("/{title}/reserve")
    fun reserveBook(@PathVariable title: String): ResponseEntity<String> {
        bookCase.reserveBook(title)
        return ResponseEntity.ok("Book reserved successfully!")
    }

    @GetMapping("/domain-error")
    fun domainError(): ResponseEntity<String> {
        // This call is expected to throw an exception due to "BadAuthor"
        bookCase.addBook(Book("whatever", "BadAuthor"))
        return ResponseEntity.ok("Should never arrive here because of domain error")
    }
}
