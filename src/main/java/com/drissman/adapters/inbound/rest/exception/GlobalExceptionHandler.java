package com.drissman.adapters.inbound.rest.exception;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(WebExchangeBindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Erreur de validation");

        log.warn("Validation error: {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("Response status error: {} - {}", ex.getStatusCode(), ex.getReason());
        int statusCode = ex.getStatusCode().value();
        String reason = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return ResponseEntity.status(statusCode)
                .body(ApiResponse.error(reason, statusCode));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Erreur interne";
        HttpStatus status;

        if (message.toLowerCase().contains("non trouvé") || message.toLowerCase().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            log.warn("Not found: {}", message);
        } else if (message.toLowerCase().contains("déjà") || message.toLowerCase().contains("already")) {
            status = HttpStatus.CONFLICT;
            log.warn("Conflict: {}", message);
        } else if (message.toLowerCase().contains("authentification") || message.toLowerCase().contains("credentials")
                || message.toLowerCase().contains("invalid")) {
            status = HttpStatus.UNAUTHORIZED;
            log.warn("Unauthorized: {}", message);
        } else if (message.toLowerCase().contains("refusé") || message.toLowerCase().contains("denied")
                || message.toLowerCase().contains("forbidden")) {
            status = HttpStatus.FORBIDDEN;
            log.warn("Forbidden: {}", message);
        } else {
            status = HttpStatus.BAD_REQUEST;
            log.warn("Bad request: {}", message);
        }

        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, status.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
