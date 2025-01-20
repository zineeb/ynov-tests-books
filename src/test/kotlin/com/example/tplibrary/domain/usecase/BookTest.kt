package com.example.tplibrary.domain.usecase

import com.example.tplibrary.domain.model.Book
import com.example.tplibrary.domain.port.BookRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSortedBy
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BookTest : FunSpec({

    test("should create a book with valid title and author") {
        val book = Book(title = "Test Book", author = "Test Author")
        book.title shouldBe "Test Book"
        book.author shouldBe "Test Author"
    }

    test("should throw an exception when title is empty") {
        shouldThrow<IllegalArgumentException> {
            Book(title = "", author = "Test Author")
        }.message shouldBe "Title must not be blank"
    }

    test("should throw an exception when author is empty") {
        shouldThrow<IllegalArgumentException> {
            Book(title = "Test Book", author = "")
        }.message shouldBe "Author must not be blank"
    }

    // Mocks for repository-based tests
    val repository = mockk<BookRepository>(relaxed = true)
    val useCase = BookCase(repository)

    test("should add a book to the repository") {
        val book = Book(title = "Test Book", author = "Test Author")
        useCase.addBook(book)
        verify { repository.save(book) }
    }

    test("should list all books sorted by title") {
        val books = listOf(
            Book(title = "The Pragmatic Programmer", author = "Andy Hunt"),
            Book(title = "Clean Code", author = "Robert Martin")
        )
        every { repository.findAll() } returns books

        val result = useCase.listBooks()
        result.shouldBeSortedBy { it.title }
    }

    test("property testing - list of books returned is sorted by title") {
        checkAll(
            Arb.list(
                Arb.bind(
                    Arb.string(minSize = 1),
                    Arb.string(minSize = 1),
                ) { title, author -> Book(title, author) },
                range = 1..10
            )
        ) { bookList ->
            every { repository.findAll() } returns bookList

            val result = useCase.listBooks()
            result.size shouldBe bookList.size
            result shouldBe bookList.sortedBy { it.title }
        }
    }
})
