package com.cobolintro.model;

import java.math.BigDecimal;

/**
 * Product model based on COBOL PRODUCTS-RECORD.
 * PIC X(5) code, PIC X(20) name, PIC 9(7)V99 price, PIC 9(5) stock.
 */
public class Product {

    private String code;
    private String name;
    private BigDecimal price;
    private int stock;

    public Product() {
    }

    public Product(String code, String name, BigDecimal price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
