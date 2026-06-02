package com.lenarsharipov.billing.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.lenarsharipov.billing.common.exceptions.ErrorResponse.Code.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Ошибка валидации входящего запроса: {}", errors);

        ErrorResponse errorResponse = new ErrorResponse(
                "Ошибка валидации параметров запроса",
                VALIDATION_ERROR.name(),
                Instant.now(),
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(SubscriptionBusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(
            SubscriptionBusinessRuleException ex
    ) {
        log.warn("Нарушение бизнес-правила подписок: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                BUSINESS_RULE_VIOLATION.name(),
                Instant.now(),
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception ex) {
        log.error("Произошла непредвиденная системная ошибка: ", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "Произошла внутренняя ошибка сервера. Пожалуйста, попробуйте позже.",
                INTERNAL_SERVER_ERROR.name(),
                Instant.now(),
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
