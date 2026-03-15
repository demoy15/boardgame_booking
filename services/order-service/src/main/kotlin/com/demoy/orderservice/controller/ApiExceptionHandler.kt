package com.demoy.orderservice.controller

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.r2dbc.BadSqlGrammarException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun onBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("message" to (ex.message ?: "invalid request")))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun onNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(mapOf("message" to (ex.message ?: "resource not found")))
    }

    @ExceptionHandler(DuplicateKeyException::class)
    fun onDuplicate(ex: DuplicateKeyException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(mapOf("message" to (ex.message ?: "duplicate resource")))
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun onIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(mapOf("message" to (ex.message ?: "data integrity violation")))
    }

    @ExceptionHandler(BadSqlGrammarException::class)
    fun onBadSql(ex: BadSqlGrammarException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("message" to "database schema is not ready"))
    }
}
