package com.example.tp_library.infrastructure.driving.controller

import com.example.tp_library.domain.model.Book
import com.example.tp_library.domain.usecase.BookCase
import com.example.tp_library.infrastructure.driving.controller.dto.BookDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookCase: BookCase) {

    @GetMapping
    fun getBooks(): ResponseEntity<List<BookDTO>> {
        val books = bookCase.listBooks()
        val dtos = books.map { BookDTO(it.title, it.author) }
        return ResponseEntity.ok(dtos)
    }

    @PostMapping
    fun addBook(@RequestBody bookDTO: BookDTO): ResponseEntity<String> {
        // On peut aussi tester le cas de titre/auteur vide
        val book = Book(bookDTO.title, bookDTO.author)
        bookCase.addBook(book)
        return ResponseEntity.ok("Book added successfully!")
    }

    @GetMapping("/domain-error")
    fun domainError(): ResponseEntity<String> {
        // Exemple d'appel
        bookCase.addBook(Book("whatever", "BadAuthor"))
        return ResponseEntity.ok("Should never arrive here because of domain error")
    }
}