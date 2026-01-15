package com.cobolmodernization.integration;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.entity.SequentialRecord;
import com.cobolmodernization.service.FileProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for FileProcessingService.
 * Validates that Java implementation produces identical results to COBOL:
 * - EJEMPLO-SECUENCIAL.cbl / SEQUENTIAL-EXAMPLE.cbl
 * - lee-escribe.cbl / read-write.cbl
 * 
 * Tests sequential file processing with auto-increment IDs and
 * line sequential text file processing.
 */
@SpringBootTest
@Transactional
class FileProcessingServiceIntegrationTest {

    @Autowired
    private FileProcessingService fileProcessingService;

    @Test
    @DisplayName("Write sequential records - equivalent to COBOL EJEMPLO-SECUENCIAL")
    void testWriteSequentialRecords() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

        assertEquals(3, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(3, result.getTotalProcessed());
    }

    @Test
    @DisplayName("Auto-increment IDs - equivalent to COBOL ADD 1 TO ID-ACTUAL")
    void testAutoIncrementIds() {
        List<String> names = Arrays.asList("First", "Second", "Third");

        fileProcessingService.writeSequentialRecords(names);

        List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

        assertEquals(3, records.size());
        assertTrue(records.get(0).getId() < records.get(1).getId());
        assertTrue(records.get(1).getId() < records.get(2).getId());
    }

    @Test
    @DisplayName("End marker 'FIN' stops processing - equivalent to COBOL IF NOMBRE-ACTUAL = 'FIN'")
    void testEndMarkerStopsProcessing() {
        List<String> names = Arrays.asList("Alice", "Bob", "FIN", "Charlie", "David");

        BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

        assertEquals(2, result.getSuccessCount());

        List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("Read all sequential records in order")
    void testReadSequentialRecordsInOrder() {
        List<String> names = Arrays.asList("Alpha", "Beta", "Gamma");

        fileProcessingService.writeSequentialRecords(names);

        List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

        assertEquals(3, records.size());
        assertEquals("Alpha", records.get(0).getName());
        assertEquals("Beta", records.get(1).getName());
        assertEquals("Gamma", records.get(2).getName());
    }

    @Test
    @DisplayName("Sequential record data matches COBOL REGISTRO structure")
    void testRecordDataStructure() {
        List<String> names = Arrays.asList("123456789012345678901234567890");

        fileProcessingService.writeSequentialRecords(names);

        List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

        assertEquals(1, records.size());
        assertEquals(30, records.get(0).getName().length());
    }

    @Test
    @DisplayName("Stream records as formatted strings - COBOL compatible format")
    void testStreamRecordsAsStrings() {
        List<String> names = Arrays.asList("Test Name");

        fileProcessingService.writeSequentialRecords(names);

        List<String> formattedRecords = fileProcessingService.streamRecordsAsStrings();

        assertEquals(1, formattedRecords.size());
        assertTrue(formattedRecords.get(0).matches("\\d{5}.*"));
    }

    @Test
    @DisplayName("Empty input list produces no records")
    void testEmptyInputList() {
        List<String> names = Arrays.asList();

        BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getTotalProcessed());

        List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();
        assertEquals(0, records.size());
    }

    @Test
    @DisplayName("Case-insensitive FIN marker")
    void testCaseInsensitiveFin() {
        List<String> names1 = Arrays.asList("Alice", "fin", "Bob");
        BatchProcessingResult result1 = fileProcessingService.writeSequentialRecords(names1);
        assertEquals(1, result1.getSuccessCount());
    }
}
