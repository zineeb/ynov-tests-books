package com.example.tplibrary.infrastructure.application

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository
import com.example.tplibrary.domain.usecase.BookCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {

    @Bean
    fun bookRepository(): BookRepository {
        return object : BookRepository {
            private val books = mutableListOf<Book>()

            override fun save(book: Book) {
                books.add(book)
            }

            override fun findAll(): List<Book> {
                return books.toList()
            }
        }
    }

    @Bean
    fun bookCase(bookRepository: BookRepository): BookCase {
        return BookCase(bookRepository)
    }
}
