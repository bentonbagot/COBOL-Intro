package com.cobolmodernization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Product entity translated from COBOL PRODUCTS-RECORD structure.
 * Original COBOL structure:
 *   05 PROD-CODE   PIC X(5)     - 5 character product code (primary key)
 *   05 PROD-NAME   PIC X(20)    - 20 character product name
 *   05 PROD-PRICE  PIC 9(7)V99  - Price with 7 digits and 2 decimal places
 *   05 PROD-STOCK  PIC 9(5)     - 5 digit stock quantity
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(name = "code", length = 5, nullable = false)
    @NotBlank(message = "Product code is required")
    @Size(max = 5, message = "Product code must be at most 5 characters")
    private String code;

    @Column(name = "name", length = 20, nullable = false)
    @NotBlank(message = "Product name is required")
    @Size(max = 20, message = "Product name must be at most 20 characters")
    private String name;

    @Column(name = "price", precision = 9, scale = 2, nullable = false)
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;

    /**
     * Reduces stock by the specified quantity.
     * Translated from COBOL UPDATE-PRODUCTS logic.
     * 
     * @param quantity the quantity to reduce
     * @throws IllegalArgumentException if quantity exceeds available stock
     */
    public void reduceStock(int quantity) {
        if (quantity > this.stock) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock for product %s. Available: %d, Requested: %d", 
                    this.code, this.stock, quantity));
        }
        this.stock -= quantity;
    }

    /**
     * Increases stock by the specified quantity.
     * 
     * @param quantity the quantity to add
     */
    public void addStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity to add must be non-negative");
        }
        this.stock += quantity;
    }
}
