package com.cobolmodernization.exception;

/**
 * Exception thrown when there is insufficient stock for a sale.
 * Translated from COBOL UPDATE-PRODUCTS logic where stock validation occurs.
 */
public class InsufficientStockException extends RuntimeException {

    private final String productCode;
    private final int availableStock;
    private final int requestedQuantity;

    public InsufficientStockException(String productCode, int availableStock, int requestedQuantity) {
        super(String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                productCode, availableStock, requestedQuantity));
        this.productCode = productCode;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}
