package com.cobolmodernization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 * Translates COBOL file status codes to HTTP responses.
 * 
 * COBOL File Status Mapping:
 * - 00 -> HTTP 200 OK
 * - 22 (duplicate) -> HTTP 409 Conflict
 * - 23 (not found) -> HTTP 404 Not Found
 * - 35 (file not exist) -> HTTP 404 Not Found
 * - Other errors -> HTTP 500 Internal Server Error
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKey(DuplicateKeyException ex) {
        Map<String, Object> response = createErrorResponse(
            "DUPLICATE_KEY",
            ex.getMessage(),
            HttpStatus.CONFLICT
        );
        response.put("key", ex.getKey());
        response.put("entityType", ex.getEntityType());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRecordNotFound(RecordNotFoundException ex) {
        Map<String, Object> response = createErrorResponse(
            "NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND
        );
        response.put("key", ex.getKey());
        response.put("entityType", ex.getEntityType());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException ex) {
        Map<String, Object> response = createErrorResponse(
            "INSUFFICIENT_STOCK",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST
        );
        response.put("productCode", ex.getProductCode());
        response.put("availableStock", ex.getAvailableStock());
        response.put("requestedQuantity", ex.getRequestedQuantity());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = createErrorResponse(
            "INVALID_ARGUMENT",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = createErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private Map<String, Object> createErrorResponse(String code, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("error", code);
        response.put("message", message);
        return response;
    }
}
