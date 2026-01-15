package com.cobolmodernization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Sale entity translated from COBOL VENTAS-RECORD structure.
 * Original COBOL structure (from excercise3-sells.cbl):
 *   05 VENTA-CODIGO    PIC X(05)  - 5 character product code
 *   05 VENTA-CANTIDAD  PIC 9(05)  - 5 digit quantity sold
 * 
 * Enhanced with auto-generated ID and timestamp for modern database storage.
 */
@Entity
@Table(name = "sales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", length = 5, nullable = false)
    @NotBlank(message = "Product code is required")
    @Size(max = 5, message = "Product code must be at most 5 characters")
    private String productCode;

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed")
    @Builder.Default
    private Boolean processed = false;

    /**
     * Marks this sale as processed (inventory updated).
     */
    public void markAsProcessed() {
        this.processed = true;
    }
}
