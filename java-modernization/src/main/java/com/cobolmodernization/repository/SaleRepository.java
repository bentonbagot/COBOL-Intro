package com.cobolmodernization.repository;

import com.cobolmodernization.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

/**
 * Repository for Sale entity.
 * Provides data access operations translated from COBOL sequential file operations.
 * 
 * COBOL File Operations Mapping (from excercise3-sells.cbl):
 * - WRITE VENTAS-RECORD -> save()
 * - READ VENTAS-FILE (sequential) -> findAll() / streamAll()
 * - File status tracking -> processed flag
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Find all sales for a specific product.
     */
    List<Sale> findByProductCode(String productCode);

    /**
     * Find all unprocessed sales.
     * Used for batch inventory updates (UPDATE-PRODUCTS logic).
     */
    List<Sale> findByProcessedFalse();

    /**
     * Find all processed sales.
     */
    List<Sale> findByProcessedTrue();

    /**
     * Find sales within a date range.
     */
    List<Sale> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Stream all unprocessed sales for batch processing.
     * Translated from COBOL sequential file reading with Java Streams.
     */
    Stream<Sale> streamByProcessedFalse();

    /**
     * Count total quantity sold for a product.
     */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sale s WHERE s.productCode = :productCode")
    Integer sumQuantityByProductCode(String productCode);

    /**
     * Count unprocessed sales.
     */
    long countByProcessedFalse();
}
