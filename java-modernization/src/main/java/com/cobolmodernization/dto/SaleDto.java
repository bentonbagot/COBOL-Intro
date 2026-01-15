package com.cobolmodernization.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDto {

    private Long id;

    @NotBlank(message = "Product code is required")
    @Size(max = 5, message = "Product code must be at most 5 characters")
    private String productCode;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private LocalDateTime createdAt;

    private Boolean processed;
}
