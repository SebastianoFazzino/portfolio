package com.sfazzino.portfolio_api.exception

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus
import java.time.Instant

data class ExceptionResponse(
    val httpStatus: HttpStatus,
    val errorCode: String,
    val message: String,
    val logLevel: LogLevel,
    val validationErrors: List<ValidationError>? = null,
    val timestamp: Instant = Instant.now()
)

data class ValidationError(
    val field: String,
    val messages: Set<String>
) {
    companion object {
        fun fromMap(errors: Map<String, List<String>>): List<ValidationError> =
            errors.map { (field, messages) -> ValidationError(field, messages.toSet()) }

        fun fromArgs(field: String, vararg errors: String): List<ValidationError> =
            listOf(ValidationError(field, errors.toSet()))
    }
}