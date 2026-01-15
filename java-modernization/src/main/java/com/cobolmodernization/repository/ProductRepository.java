package com.cobolmodernization.repository;

import com.cobolmodernization.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entity.
 * Provides data access operations translated from COBOL indexed file operations.
 * 
 * COBOL File Operations Mapping:
 * - READ ... KEY IS -> findById() / findByCode()
 * - WRITE -> save()
 * - REWRITE -> save() (update)
 * - DELETE -> deleteById()
 * - START/READ NEXT -> findAll() with pagination
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Find product by code.
     * Equivalent to COBOL: READ PRODUCTS-FILE KEY IS PROD-CODE
     */
    Optional<Product> findByCode(String code);

    /**
     * Check if product exists by code.
     * Used for duplicate detection (COBOL file status 22).
     */
    boolean existsByCode(String code);

    /**
     * Find all products with stock below threshold.
     * Useful for inventory management.
     */
    List<Product> findByStockLessThan(Integer threshold);

    /**
     * Find all products with stock greater than zero.
     */
    List<Product> findByStockGreaterThan(Integer threshold);

    /**
     * Update product stock directly.
     * Translated from COBOL UPDATE-PRODUCTS batch processing.
     */
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.code = :code AND p.stock >= :quantity")
    int reduceStock(@Param("code") String code, @Param("quantity") Integer quantity);

    /**
     * Find products by name pattern.
     * Enhanced search capability beyond original COBOL.
     */
    List<Product> findByNameContainingIgnoreCase(String namePart);
}
