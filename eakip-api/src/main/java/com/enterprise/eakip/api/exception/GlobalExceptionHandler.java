package com.enterprise.eakip.api.exception;

import com.enterprise.eakip.core.common.dto.ApiErrorResponse;
import com.enterprise.eakip.core.domain.exception.EntityNotFoundException;
import com.enterprise.eakip.core.domain.exception.ResourceConflictException;
import com.enterprise.eakip.core.domain.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceConflict(ResourceConflictException ex) {
        log.warn("Conflict detected: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, "RESOURCE_CONFLICT", ex.getMessage(), Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to access this resource", Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.warn("Validation failure: {}", errors);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request fields validation failed", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Internal server error occurred: ", ex);
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred on the server", Collections.singletonList(ex.getMessage()));
    }

    private ResponseEntity<ApiErrorResponse> buildResponseEntity(HttpStatus status, String errorCode, String message, List<String> details) {
        String correlationId = MDC.get("correlationId");
        ApiErrorResponse error = ApiErrorResponse.of(errorCode, message, correlationId, details);
        return new ResponseEntity<>(error, status);
    }
}
