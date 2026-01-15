package com.cobolmodernization.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @NotBlank(message = "Product code is required")
    @Size(max = 5, message = "Product code must be at most 5 characters")
    private String code;

    @NotBlank(message = "Product name is required")
    @Size(max = 20, message = "Product name must be at most 20 characters")
    private String name;

    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;
}
