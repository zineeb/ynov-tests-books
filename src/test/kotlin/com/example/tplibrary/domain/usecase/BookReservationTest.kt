package com.example.tplibrary.domain.usecase

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository
import com.example.tplibrary.domain.usecase.BookCase
import com.example.tplibrary.domain.usecase.BookDomainException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

/**
 * Unit tests focusing on the reservation logic in BookCase.
 */
class BookReservationTest : FunSpec({

    val repository = mockk<BookRepository>()
    val useCase = BookCase(repository)

    beforeTest {
        io.mockk.clearMocks(repository)
    }

    test("should reserve a non-reserved book") {
        val book = Book(title = "New Title", author = "An Author", reserved = false)

        every { repository.findByTitle("New Title") } returns book
        every { repository.update(any()) } just runs

        useCase.reserveBook("New Title")

        verify {
            repository.update(withArg {
                it.title shouldBe "New Title"
                it.reserved shouldBe true
            })
        }
    }

    test("should fail when book is already reserved") {
        val alreadyReservedBook = Book(title = "Reserved Book", author = "An Author", reserved = true)

        every { repository.findByTitle("Reserved Book") } returns alreadyReservedBook

        val ex = shouldThrow<BookDomainException> {
            useCase.reserveBook("Reserved Book")
        }
        ex.message shouldBe "Book is already reserved"

        verify(exactly = 0) { repository.update(any()) }
    }

    test("should fail when book is not found") {
        every { repository.findByTitle("Missing Book") } returns null

        val ex = shouldThrow<BookDomainException> {
            useCase.reserveBook("Missing Book")
        }
        ex.message shouldBe "Book with title 'Missing Book' not found"
    }
})
