package com.cobolmodernization.repository;

import com.cobolmodernization.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Customer entity.
 * Provides data access operations translated from COBOL indexed file operations.
 * 
 * COBOL File Operations Mapping (from EJEMPLO-INDEXADO.cbl):
 * - READ ARCHIVO-CLIENTES KEY IS CLAVE-CLIENTE -> findByCustomerId()
 * - WRITE REGISTRO-CLIENTE -> save()
 * - INVALID KEY (duplicate) -> existsByCustomerId() check before save
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * Find customer by ID.
     * Equivalent to COBOL: READ ARCHIVO-CLIENTES KEY IS CLAVE-CLIENTE
     */
    Optional<Customer> findByCustomerId(String customerId);

    /**
     * Check if customer exists by ID.
     * Used for duplicate detection (COBOL INVALID KEY condition).
     */
    boolean existsByCustomerId(String customerId);

    /**
     * Find customers by name pattern.
     * Enhanced search capability beyond original COBOL.
     */
    List<Customer> findByNameContainingIgnoreCase(String namePart);

    /**
     * Find customers by phone pattern.
     */
    List<Customer> findByPhoneContaining(String phonePart);
}
