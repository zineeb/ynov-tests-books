package com.example.tp_library.domain.model

data class Book(val title: String, val author: String) {
    init {
        require(author.isNotBlank()) { "Author must not be blank" }
        require(title.isNotBlank()) { "Title must not be blank" }
    }
}