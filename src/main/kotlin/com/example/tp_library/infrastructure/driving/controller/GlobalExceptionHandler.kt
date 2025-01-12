package com.example.tp_library.infrastructure.driving.controller

import com.example.tp_library.domain.usecase.BookDomainException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BookDomainException::class)
    fun handleDomain(ex: BookDomainException): ResponseEntity<String> {
        return ResponseEntity.unprocessableEntity().body("Domain error: ${ex.message}")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArg(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}
