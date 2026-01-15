package com.cobolmodernization.model;

public class SalesRecord {

    private final String productCode;
    private final int quantity;

    private SalesRecord(Builder builder) {
        this.productCode = builder.productCode;
        this.quantity = builder.quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String productCode;
        private int quantity;

        public Builder productCode(String productCode) {
            this.productCode = productCode;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public SalesRecord build() {
            return new SalesRecord(this);
        }
    }

    @Override
    public String toString() {
        return "SalesRecord{" +
                "productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
