package com.work.taskCatalog.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.ServerWebInputException
import java.sql.SQLException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // 400 - Ошибки валидации (@Valid)
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(ex: WebExchangeBindException): ResponseEntity<MutableMap<String, Any>> {
        val errors = mutableMapOf<String, Any>()
        errors["timestamp"] = LocalDateTime.now().toString()
        errors["message"] = "Validation failed"
        errors["errors"] = ex.bindingResult.fieldErrors.map { error ->
            mapOf(
                "field" to error.field,
                "defaultMessage" to (error.defaultMessage ?: "Invalid value"),
                "rejectedValue" to (error.rejectedValue ?: "")
            )
        }
        log.error("Validation error: $ex")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleHttpMessageNotReadableException(ex: ServerWebInputException): ResponseEntity<MutableMap<String, Any>> {
        val cause = ex.cause
        print("exception: $ex, cause = $cause stackTrace: ${ex.stackTraceToString()}")
        val errors = mutableMapOf<String, Any>()
        errors["timestamp"] = LocalDateTime.now().toString()
        errors["message"] = ex.message
        log.error("Web Input error: $ex, \nstackTrace: ${ex.stackTraceToString()}")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }


    // 503 - Ошибка подключения к БД
    @ExceptionHandler(SQLException::class)
    fun handleSql(ex: SQLException): ResponseEntity<MutableMap<String, Any>> {
        val errors = mutableMapOf<String, Any>()
        errors["timestamp"] = LocalDateTime.now().toString()
        errors["message"] = "DataBase error: $ex"
        log.error("Internal SQL error: $ex, \nstackTrace: ${ex.stackTraceToString()}")
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(errors)
    }

    // 500 - Непредвиденная ошибка
    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<MutableMap<String, Any>> {
        val errors = mutableMapOf<String, Any>()
        errors["timestamp"] = LocalDateTime.now().toString()
        errors["message"] = "Internal error: $ex"
        log.error("Unexpected error: $ex, \nstackTrace: ${ex.stackTraceToString()}")
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errors)
    }
}