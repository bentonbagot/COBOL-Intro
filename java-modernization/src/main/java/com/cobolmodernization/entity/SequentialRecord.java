package com.cobolmodernization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SequentialRecord entity translated from COBOL REGISTRO structure.
 * Original COBOL structure (from EJEMPLO-SECUENCIAL.cbl):
 *   05 IDNUM   PIC 9(5)   - 5 digit auto-increment ID
 *   05 NOMBRE  PIC X(30)  - 30 character name
 * 
 * This entity represents sequential file records with auto-increment IDs,
 * modernized to use JPA auto-generation instead of manual ID tracking.
 */
@Entity
@Table(name = "sequential_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SequentialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    @NotBlank(message = "Name is required")
    @Size(max = 30, message = "Name must be at most 30 characters")
    private String name;
}
