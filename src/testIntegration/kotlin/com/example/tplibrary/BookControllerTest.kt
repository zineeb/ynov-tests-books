package com.example.tplibrary

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.usecase.BookCase
import com.example.tplibrary.domain.usecase.BookDomainException
import com.example.tplibrary.infrastructure.driving.controller.BookController
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

/**
 * Integration test for BookController using MockMVC.
 * Focuses on controller endpoints and behavior.
 */
@WebMvcTest(BookController::class)
class BookControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var bookCase: BookCase

    @Test
    fun `GET books should return list of books including reservation status`() {
        every { bookCase.listBooks() } returns listOf(
            Book("1984", "George Orwell", reserved = true),
            Book("Brave New World", "Aldous Huxley", reserved = false)
        )

        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].title") { value("1984") }
                jsonPath("$[0].reserved") { value(true) }
                jsonPath("$[1].title") { value("Brave New World") }
                jsonPath("$[1].reserved") { value(false) }
            }

        verify(exactly = 1) { bookCase.listBooks() }
    }

    @Test
    fun `POST books should add a book successfully`() {
        every { bookCase.addBook(any()) } returns Unit

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "Le Petit Prince", "author": "Antoine de Saint-Exupéry"}"""
        }.andExpect {
            status { isOk() }
            content { string("Book added successfully!") }
        }

        verify(exactly = 1) { bookCase.addBook(any()) }
    }

    @Test
    fun `POST books should return 400 for invalid input`() {
        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "", "author": "Unknown"}"""
        }.andExpect {
            status { isBadRequest() }
            content { string("Le titre ne peut pas être vide") }
        }
    }

    @Test
    fun `domain error should return 422`() {
        every { bookCase.addBook(any()) } throws BookDomainException("Author is blacklisted")

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "Whatever", "author": "BadAuthor"}"""
        }.andExpect {
            status { isUnprocessableEntity() }
            content { string("Domain error: Author is blacklisted") }
        }
    }

    // Reservation tests
    @Test
    fun `POST reserve book - success`() {
        every { bookCase.reserveBook("SomeTitle") } just runs

        mockMvc.post("/books/SomeTitle/reserve")
            .andExpect {
                status { isOk() }
                content { string("Book reserved successfully!") }
            }

        verify { bookCase.reserveBook("SomeTitle") }
    }

    @Test
    fun `POST reserve book - already reserved`() {
        every { bookCase.reserveBook("ReservedTitle") } throws BookDomainException("Book is already reserved")

        mockMvc.post("/books/ReservedTitle/reserve")
            .andExpect {
                status { isUnprocessableEntity() }
                content { string("Domain error: Book is already reserved") }
            }
    }
}
