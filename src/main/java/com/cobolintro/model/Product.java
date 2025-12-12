package com.cobolintro.model;

import java.math.BigDecimal;

/**
 * Java POJO representing the COBOL PRODUCTS-RECORD structure.
 * Maps to the record definition in English-COBOL/createdat.cbl (lines 16-20).
 */
public class Product {
    private String prodCode;
    private String prodName;
    private BigDecimal prodPrice;
    private int prodStock;

    public Product() {
    }

    public Product(String prodCode, String prodName, BigDecimal prodPrice, int prodStock) {
        this.prodCode = prodCode;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodStock = prodStock;
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public BigDecimal getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(BigDecimal prodPrice) {
        this.prodPrice = prodPrice;
    }

    public int getProdStock() {
        return prodStock;
    }

    public void setProdStock(int prodStock) {
        this.prodStock = prodStock;
    }
}
