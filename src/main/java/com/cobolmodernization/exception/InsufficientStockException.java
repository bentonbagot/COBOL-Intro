package com.cobolmodernization.exception;

public class InsufficientStockException extends RuntimeException {

    private final String productCode;
    private final int requestedQuantity;
    private final int availableStock;

    public InsufficientStockException(String productCode, int requestedQuantity, int availableStock) {
        super("Insufficient stock for product '" + productCode + "': requested " + requestedQuantity + ", available " + availableStock);
        this.productCode = productCode;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }
}
