package com.work.taskCatalog.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {
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

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }

    //требуется проверить!!!
    //400 - Нарушение constraint (CHECK, NOT NULL)
//    @ExceptionHandler(DataIntegrityViolationException::class)
    @ExceptionHandler(Exception::class)
    fun handleConstraint(ex: Exception): ResponseEntity<MutableMap<String, Any>> {
        val message = when {
            ex.message?.contains("CHECK constraint") == true ->
                "Invalid value for field: ${ex.message}"
            ex.message?.contains("not-null") == true ->
                "Field cannot be null: ${ex.message}"
            else -> "Data integrity violation: ${ex.message}"
        }
        val errors = mutableMapOf<String, Any>()
        errors["timestamp"] = LocalDateTime.now().toString()
        errors["message"] = message
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleHttpMessageNotReadableException(ex: ServerWebInputException): ResponseEntity<MutableMap<String, Any>> {
        val cause = ex.cause// ServerWebInputException -> DecodingException -> MissingKotlinParameterException
        print("exception: $ex, cause = $cause stackTrace: ${ex.stackTraceToString()}")
        val errors = mutableMapOf<String, Any>()
        errors["timestamp"] = LocalDateTime.now().toString()
        errors["message"] = ex.message
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }

//    // 409 - Дубликат (unique constraint)
//    @ExceptionHandler(DataIntegrityViolationException::class)
//    fun handleDuplicate(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
//        return if (ex.message?.contains("unique") == true) {
//            ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(ErrorResponse(
//                    status = 409,
//                    error = "Conflict",
//                    code = ErrorCode.DUPLICATE_ENTRY,
//                    message = "Record already exists"
//                ))
//        } else {
//            throw ex // Прокидываем для обработки другим handler
//        }
//    }


//    // 503 - Ошибка подключения к БД
//    @ExceptionHandler(SQLException::class)
//    fun handleSql(ex: SQLException): ResponseEntity<ErrorResponse> {
//        return ResponseEntity
//            .status(HttpStatus.SERVICE_UNAVAILABLE)
//            .body(ErrorResponse(
//                status = 503,
//                error = "Service Unavailable",
//                code = ErrorCode.SERVICE_UNAVAILABLE,
//                message = "Database connection error"
//            ))
//    }
//    // 500 - Непредвиденная ошибка
//    @ExceptionHandler(Exception::class)
//    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
//        return ResponseEntity
//            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//            .body(ErrorResponse(
//                status = 500,
//                error = "Internal Server Error",
//                code = ErrorCode.INTERNAL_ERROR,
//                message = "An unexpected error occurred"
//            ))
//    }
}