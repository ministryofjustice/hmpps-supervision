package uk.gov.justice.digital.hmpps.supervision.controller.advice

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import uk.gov.justice.digital.hmpps.supervision.model.ErrorResponse
import uk.gov.justice.digital.hmpps.supervision.model.ErrorResponse.Field

@RestControllerAdvice(basePackages = ["uk.gov.justice.digital.hmpps"])
class ExceptionHandler {
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException) = e.withStatus(FORBIDDEN)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException) = e.withStatus(
        status = BAD_REQUEST,
        message = "Validation failure",
        fields = e.bindingResult.fieldErrors.map { Field(it.code, it.defaultMessage, it.field) }
    )

    @ExceptionHandler(value = [ConstraintViolationException::class, IllegalArgumentException::class])
    fun handleBadRequest(e: Exception) = e.withStatus(BAD_REQUEST)

    fun Exception.withStatus(status: HttpStatus, message: String? = this.message, fields: List<Field>? = null) =
        ResponseEntity.status(status).body(ErrorResponse(status.value(), message, fields))
}
