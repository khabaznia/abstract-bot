package com.khabaznia.bots.core.endpoint

import com.khabaznia.bots.core.integration.dto.ErrorResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandlingController {

    private static final String FORM_VALIDATION_ERROR_MESSAGE = "Form validation error";
    private static final String FORM_VALIDATION_ERROR_KEY = "FORM_VALIDATION_ERROR";
    private static final String ILLEGAL_ARGUMENT_ERROR_KEY = "ILLEGAL_ARGUMENT_ERROR";

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponseDto> handleIncorrectArgumentInRequest(IllegalArgumentException ex) {
        ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorResponseDto(
                        errorMessage: ex.message,
                        errorCode: ILLEGAL_ARGUMENT_ERROR_KEY))
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseDto> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(errorCode: FORM_VALIDATION_ERROR_KEY,
                        errorMessage: FORM_VALIDATION_ERROR_MESSAGE,
                        errorDetails: getErrorMessages(ex)))
    }

    private static Map<String, String> getErrorMessages(MethodArgumentNotValidException ex) {
        ex?.bindingResult
                ?.allErrors
                ?.collect { it as FieldError }
                ?.collectEntries { [(it.field): it.defaultMessage] }
    }

}
