package com.cobolmodernization.repository;

import com.cobolmodernization.entity.SequentialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

/**
 * Repository for SequentialRecord entity.
 * Provides data access operations translated from COBOL sequential file operations.
 * 
 * COBOL File Operations Mapping (from EJEMPLO-SECUENCIAL.cbl):
 * - WRITE REGISTRO -> save()
 * - Sequential read -> findAll() / streamAll()
 * - Auto-increment ID -> JPA GenerationType.IDENTITY
 */
@Repository
public interface SequentialRecordRepository extends JpaRepository<SequentialRecord, Long> {

    /**
     * Find records by name pattern.
     */
    List<SequentialRecord> findByNameContainingIgnoreCase(String namePart);

    /**
     * Stream all records for sequential processing.
     * Translated from COBOL sequential file reading with Java Streams.
     */
    Stream<SequentialRecord> streamAllBy();

    /**
     * Find records ordered by ID (sequential order).
     */
    List<SequentialRecord> findAllByOrderByIdAsc();
}
