package com.cobolmodernization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customer entity translated from COBOL REGISTRO-CLIENTE structure.
 * Original COBOL structure (from EJEMPLO-INDEXADO.cbl):
 *   05 CLAVE-CLIENTE   PIC X(10)  - 10 character customer key (primary key)
 *   05 NOMBRE-CLIENTE  PIC X(30)  - 30 character customer name
 *   05 TELEFONO        PIC X(15)  - 15 character phone number
 */
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "customer_id", length = 10, nullable = false)
    @NotBlank(message = "Customer ID is required")
    @Size(max = 10, message = "Customer ID must be at most 10 characters")
    private String customerId;

    @Column(name = "name", length = 30, nullable = false)
    @NotBlank(message = "Customer name is required")
    @Size(max = 30, message = "Customer name must be at most 30 characters")
    private String name;

    @Column(name = "phone", length = 15)
    @Size(max = 15, message = "Phone number must be at most 15 characters")
    private String phone;
}
