package com.example.tp_library.infrastructure.driving.controller

import com.example.tp_library.domain.usecase.BookDomainException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BookDomainException::class)
    fun handleDomain(ex: BookDomainException): ResponseEntity<String> {
        // On choisit un code HTTP, ex. 422
        return ResponseEntity.unprocessableEntity().body("Domain error: ${ex.message}")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArg(ex: IllegalArgumentException): ResponseEntity<String> {
        // On renvoie 400 si un Book a un titre/auteur vide
        return ResponseEntity.badRequest().body(ex.message)
    }
}
