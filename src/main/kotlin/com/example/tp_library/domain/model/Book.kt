package com.example.tp_library.domain.model

data class Book(val title: String, val author: String) {
    init {
        require(author.trim().isNotEmpty()) { "Author must not be blank" }
        require(title.trim().isNotEmpty()) { "Title must not be blank" }
    }
}