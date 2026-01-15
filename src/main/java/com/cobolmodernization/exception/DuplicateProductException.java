package com.cobolmodernization.exception;

public class DuplicateProductException extends RuntimeException {

    private final String productCode;

    public DuplicateProductException(String productCode) {
        super("Product with code '" + productCode + "' already exists");
        this.productCode = productCode;
    }

    public DuplicateProductException(String productCode, Throwable cause) {
        super("Product with code '" + productCode + "' already exists", cause);
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }
}
