package com.cobolintro.model;

/**
 * Java POJO representing the COBOL VENTAS-RECORD structure.
 * Maps to the record definition in English-COBOL/excercise3-sells.cbl (lines 34-36).
 */
public class Sale {
    private String saleCode;
    private int saleQuantity;

    public Sale() {
    }

    public Sale(String saleCode, int saleQuantity) {
        this.saleCode = saleCode;
        this.saleQuantity = saleQuantity;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public int getSaleQuantity() {
        return saleQuantity;
    }

    public void setSaleQuantity(int saleQuantity) {
        this.saleQuantity = saleQuantity;
    }
}
