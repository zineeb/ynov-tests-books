package com.example.tplibrary.infrastructure.application

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository
import com.example.tplibrary.domain.usecase.BookCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Spring configuration providing beans for repository and use case.
 * In real scenarios, you might replace the in-memory implementation with a real DB adapter.
 */
@Configuration
open class UseCasesConfiguration {

    @Bean
    open fun bookRepository(): BookRepository {
        // In-memory implementation for demonstration
        return object : BookRepository {
            private val books = mutableListOf<Book>()

            override fun save(book: Book) {
                books.add(book)
            }

            override fun findAll(): List<Book> = books.toList()

            override fun findByTitle(title: String): Book? {
                return books.find { it.title == title }
            }

            override fun update(book: Book) {
                // For the in-memory version, we remove the old one and add the updated
                books.removeIf { it.title == book.title }
                books.add(book)
            }
        }
    }

    @Bean
    open fun bookCase(bookRepository: BookRepository): BookCase {
        return BookCase(bookRepository)
    }
}
