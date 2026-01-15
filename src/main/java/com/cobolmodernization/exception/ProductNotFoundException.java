package com.cobolmodernization.exception;

public class ProductNotFoundException extends RuntimeException {

    private final String productCode;

    public ProductNotFoundException(String productCode) {
        super("Product with code '" + productCode + "' not found");
        this.productCode = productCode;
    }

    public ProductNotFoundException(String productCode, Throwable cause) {
        super("Product with code '" + productCode + "' not found", cause);
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }
}
