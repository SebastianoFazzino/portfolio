package com.sfazzino.portfolio_api.exception

object ErrorCodes {
  const val VALIDATION_ERROR = "validation_error"
  const val BAD_REQUEST = "bad_request"
  const val TOO_MANY_REQUESTS = "too_many_requests"

  const val EMAIL_SEND_FAILED = "email_send_failed"
  const val CONTACT_REJECTED = "contact_rejected"

  const val RESOURCE_NOT_FOUND = "resource_not_found"
  const val METHOD_NOT_ALLOWED = "method_not_allowed"

  const val MISSING_API_KEY = "missing_api_key"
  const val EXPIRED_API_KEY = "expired_api_key"
  const val INVALID_API_KEY = "invalid_api_key"

  const val UNAUTHORIZED = "unauthorized"
  const val FORBIDDEN = "forbidden"
  const val INTERNAL_SERVER_ERROR = "internal_server_error"
  const val UNKNOWN_ERROR = "unknown_error"
}