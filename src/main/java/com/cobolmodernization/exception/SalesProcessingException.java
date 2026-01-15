package com.cobolmodernization.exception;

public class SalesProcessingException extends RuntimeException {

    public SalesProcessingException(String message) {
        super(message);
    }

    public SalesProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
