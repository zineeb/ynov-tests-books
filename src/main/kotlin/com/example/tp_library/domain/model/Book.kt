package com.example.tp_library.domain.model

data class Book(val title: String, val author: String) {
    init {
        require(title.isNotEmpty()) { "Title must not be blank" }
        require(author.isNotEmpty()) { "Author must not be blank" }
    }
}
