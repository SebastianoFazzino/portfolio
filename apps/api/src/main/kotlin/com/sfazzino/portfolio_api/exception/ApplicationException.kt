package com.sfazzino.portfolio_api.exception

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

class ApplicationException(
  val httpStatus: HttpStatus,
  val errorCode: String,
  override val message: String,
  val logLevel: LogLevel = LogLevel.ERROR,
  val validationErrors: List<ValidationError>? = null,
) : RuntimeException(message) {

  companion object {
    fun badRequest(message: String, errorCode: String? = null) =
      ApplicationException(
        httpStatus = HttpStatus.BAD_REQUEST,
        errorCode = errorCode ?: ErrorCodes.BAD_REQUEST,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun validationError(message: String, validationErrors: Map<String, List<String>>) =
      ApplicationException(
        httpStatus = HttpStatus.BAD_REQUEST,
        errorCode = ErrorCodes.VALIDATION_ERROR,
        message = message,
        logLevel = LogLevel.WARN,
        validationErrors = ValidationError.fromMap(validationErrors)
      )

    fun tooManyRequests(message: String = "Too many requests") =
      ApplicationException(
        httpStatus = HttpStatus.TOO_MANY_REQUESTS,
        errorCode = ErrorCodes.TOO_MANY_REQUESTS,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun emailSendFailed(message: String = "Failed to send message") =
      ApplicationException(
        httpStatus = HttpStatus.BAD_GATEWAY,
        errorCode = ErrorCodes.EMAIL_SEND_FAILED,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun contactRejected(message: String = "Message rejected") =
      ApplicationException(
        httpStatus = HttpStatus.BAD_REQUEST,
        errorCode = ErrorCodes.CONTACT_REJECTED,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun notFound(message: String = "Resource not found") =
      ApplicationException(
        httpStatus = HttpStatus.NOT_FOUND,
        errorCode = ErrorCodes.RESOURCE_NOT_FOUND,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun methodNotAllowed(message: String = "Method not allowed") =
      ApplicationException(
        httpStatus = HttpStatus.METHOD_NOT_ALLOWED,
        errorCode = ErrorCodes.METHOD_NOT_ALLOWED,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun unauthorized(message: String) =
      ApplicationException(
        httpStatus = HttpStatus.UNAUTHORIZED,
        errorCode = ErrorCodes.UNAUTHORIZED,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun forbidden(message: String) =
      ApplicationException(
        httpStatus = HttpStatus.FORBIDDEN,
        errorCode = ErrorCodes.FORBIDDEN,
        message = message,
        logLevel = LogLevel.WARN
      )

    fun internalServerError(message: String = "Internal server error") =
      ApplicationException(
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        errorCode = ErrorCodes.INTERNAL_SERVER_ERROR,
        message = message,
        logLevel = LogLevel.ERROR
      )

    fun unknownError(message: String = "An unexpected error occurred") =
      ApplicationException(
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        errorCode = ErrorCodes.UNKNOWN_ERROR,
        message = message,
        logLevel = LogLevel.ERROR
      )
  }
}
