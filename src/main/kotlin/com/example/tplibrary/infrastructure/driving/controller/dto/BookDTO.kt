package com.example.tplibrary.infrastructure.driving.controller.dto

/**
 * DTO (Data Transfer Object) for a Book.
 * Includes the 'reserved' field to indicate if a book is currently reserved.
 */
data class BookDTO(
    val title: String,
    val author: String,
    val reserved: Boolean = false
)
