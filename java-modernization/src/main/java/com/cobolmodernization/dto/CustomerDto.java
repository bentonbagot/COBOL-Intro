package com.cobolmodernization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    @NotBlank(message = "Customer ID is required")
    @Size(max = 10, message = "Customer ID must be at most 10 characters")
    private String customerId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 30, message = "Customer name must be at most 30 characters")
    private String name;

    @Size(max = 15, message = "Phone number must be at most 15 characters")
    private String phone;
}
