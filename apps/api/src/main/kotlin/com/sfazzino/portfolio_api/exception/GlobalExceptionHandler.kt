package com.sfazzino.portfolio_api.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(ApplicationException::class)
  fun handleApplicationException(ex: ApplicationException): ResponseEntity<ExceptionResponse> =
    respond(ex)

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse> {
    val target = ex.bindingResult.objectName
    val validationErrors = ex.bindingResult.fieldErrors
      .groupBy { it.field }
      .mapValues { (_, errs) -> errs.map { it.defaultMessage ?: "Invalid value" } }

    return respond(
      ApplicationException.validationError(
        message = "Validation failed for $target with ${validationErrors.size} error(s).",
        validationErrors = validationErrors
      )
    )
  }

  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun handleMalformedJson(): ResponseEntity<ExceptionResponse> =
    respond(
      ApplicationException.validationError(
        message = "Malformed JSON request body.",
        validationErrors = mapOf("body" to listOf("Malformed JSON"))
      )
    )

  @ExceptionHandler(NoResourceFoundException::class)
  fun handleNoResourceFound(): ResponseEntity<ExceptionResponse> =
    respond(
      ApplicationException(
        httpStatus = HttpStatus.NOT_FOUND,
        errorCode = ErrorCodes.RESOURCE_NOT_FOUND,
        message = "The requested resource was not found.",
        logLevel = LogLevel.WARN
      )
    )

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  fun handleMethodNotAllowed(): ResponseEntity<ExceptionResponse> =
    respond(
      ApplicationException(
        httpStatus = HttpStatus.METHOD_NOT_ALLOWED,
        errorCode = ErrorCodes.METHOD_NOT_ALLOWED,
        message = "Method not allowed.",
        logLevel = LogLevel.WARN
      )
    )

  @ExceptionHandler(Exception::class)
  fun handleGeneric(ex: Exception, request: HttpServletRequest): ResponseEntity<ExceptionResponse> {

    // Log full detail internally
    log.error(
      "Unexpected error while processing {} message={} cause={}",
      request.requestURI,
      ex.message,
      ex.cause?.javaClass?.name,
      ex
    )

    val safe = ExceptionResponse(
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
      errorCode = ErrorCodes.UNKNOWN_ERROR,
      message = "An unexpected error occurred while accessing ${request.requestURI}.",
      logLevel = LogLevel.ERROR,
      validationErrors = null,
    )

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(safe)
  }

  private fun respond(ex: ApplicationException): ResponseEntity<ExceptionResponse> {
    logByLevel(ex.logLevel, ex)
    return ResponseEntity.status(ex.httpStatus).body(ex.toResponse())
  }

  private fun ApplicationException.toResponse(): ExceptionResponse =
    ExceptionResponse(
      httpStatus = this.httpStatus,
      errorCode = this.errorCode,
      message = this.message,
      logLevel = this.logLevel,
      validationErrors = this.validationErrors,
    )

  private fun logByLevel(level: LogLevel, ex: ApplicationException) {
    when (level) {
      LogLevel.ERROR -> log.error("{}", ex.message)
      LogLevel.WARN -> log.warn("{}", ex.message)
      else -> log.info("{}", ex.message)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
  }
}
