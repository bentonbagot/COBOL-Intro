package com.cobolintro.model;

/**
 * Sale model based on COBOL SELLS record structure.
 * PIC X(5) productCode, PIC 9(5) quantity.
 */
public class Sale {

    private String productCode;
    private int quantity;

    public Sale() {
    }

    public Sale(String productCode, int quantity) {
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
