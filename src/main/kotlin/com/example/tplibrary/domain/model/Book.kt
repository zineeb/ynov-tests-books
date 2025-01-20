package com.example.tplibrary.domain.model

/**
 * Domain model for a Book.
 * Validates that title and author are not empty.
 * Contains a 'reserved' flag indicating reservation status.
 */
data class Book(
    val title: String,
    val author: String,
    val reserved: Boolean = false
) {
    init {
        require(title.isNotEmpty()) { "Title must not be blank" }
        require(author.isNotEmpty()) { "Author must not be blank" }
    }
}
