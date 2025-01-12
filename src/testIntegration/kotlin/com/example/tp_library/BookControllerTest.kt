package com.example.tp_library

import com.example.tp_library.domain.usecase.BookCase
import com.example.tp_library.domain.usecase.BookDomainException
import com.example.tp_library.infrastructure.driving.controller.BookController
import com.example.tp_library.infrastructure.driving.controller.dto.BookDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(BookController::class)
class BookControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var bookCase: BookCase

    @Test
    fun `GET books should return list of books`() {
        every { bookCase.listBooks() } returns listOf(
            com.example.tp_library.domain.model.Book("1984", "George Orwell")
        )

        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].title") { value("1984") }
                jsonPath("$[0].author") { value("George Orwell") }
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
}