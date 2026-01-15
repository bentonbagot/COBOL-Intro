package com.cobolmodernization.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cobolmodernization.exception.CustomerNotFoundException;
import com.cobolmodernization.exception.DuplicateCustomerException;
import com.cobolmodernization.exception.DuplicateProductException;
import com.cobolmodernization.exception.InsufficientStockException;
import com.cobolmodernization.exception.ProductNotFoundException;
import com.cobolmodernization.exception.SalesProcessingException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProduct(DuplicateProductException ex) {
        logger.error("Duplicate product error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("DUPLICATE_PRODUCT", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        logger.error("Product not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("PRODUCT_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCustomer(DuplicateCustomerException ex) {
        logger.error("Duplicate customer error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("DUPLICATE_CUSTOMER", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
        logger.error("Customer not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("CUSTOMER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        logger.error("Insufficient stock: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("INSUFFICIENT_STOCK", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(SalesProcessingException.class)
    public ResponseEntity<ErrorResponse> handleSalesProcessing(SalesProcessingException ex) {
        logger.error("Sales processing error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("SALES_PROCESSING_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public static class ErrorResponse {
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
