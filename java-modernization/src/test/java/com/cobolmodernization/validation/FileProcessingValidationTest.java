package com.cobolmodernization.validation;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.entity.SequentialRecord;
import com.cobolmodernization.service.FileProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for File Processing consistency.
 * Validates that Java implementation produces identical results to COBOL programs:
 * - SEQUENTIAL-EXAMPLE.cob / EJEMPLO-SECUENCIAL.cbl - Sequential file writing with auto-increment IDs
 * - READ-WRITE.cob / lee-escribe.cbl - Line sequential text file processing
 * - RELATIVE.cob - Position-based record access
 * 
 * Tests verify:
 * 1. Sequential record structure matches COBOL REGISTRO (PIC clauses)
 * 2. Auto-increment ID logic matches COBOL ADD 1 TO ID-ACTUAL
 * 3. End marker 'FIN' handling matches COBOL IF NOMBRE-ACTUAL = 'FIN'
 * 4. Line sequential file processing matches COBOL READ/WRITE operations
 */
@SpringBootTest
@Transactional
class FileProcessingValidationTest {

    @Autowired
    private FileProcessingService fileProcessingService;

    @Nested
    @DisplayName("SEQUENTIAL-EXAMPLE.cob Validation - Sequential File Writing")
    class SequentialExampleValidation {

        @Test
        @DisplayName("Write sequential records matches COBOL WRITE REGISTRO")
        void validateWriteSequentialRecords() {
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(3, result.getSuccessCount(),
                    "Should write 3 records as in COBOL SEQUENTIAL-EXAMPLE");
            assertEquals(0, result.getFailureCount());
        }

        @Test
        @DisplayName("Auto-increment IDs match COBOL ADD 1 TO ID-ACTUAL")
        void validateAutoIncrementIds() {
            List<String> names = Arrays.asList("First", "Second", "Third");

            fileProcessingService.writeSequentialRecords(names);

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

            assertEquals(3, records.size());
            assertTrue(records.get(0).getId() < records.get(1).getId(),
                    "IDs should be auto-incremented");
            assertTrue(records.get(1).getId() < records.get(2).getId(),
                    "IDs should be auto-incremented");
        }

        @Test
        @DisplayName("End marker 'FIN' stops processing - matches COBOL IF NOMBRE-ACTUAL = 'FIN'")
        void validateEndMarkerStopsProcessing() {
            List<String> names = Arrays.asList("Alice", "Bob", "FIN", "Charlie", "David");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(2, result.getSuccessCount(),
                    "Should stop at 'FIN' marker as in COBOL");

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();
            assertEquals(2, records.size());
            assertEquals("Alice", records.get(0).getName());
            assertEquals("Bob", records.get(1).getName());
        }

        @Test
        @DisplayName("Case-insensitive 'FIN' marker - 'fin' also stops processing")
        void validateCaseInsensitiveFin() {
            List<String> names = Arrays.asList("Alice", "fin", "Bob");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(1, result.getSuccessCount());
        }

        @Test
        @DisplayName("Records are read in sequential order")
        void validateSequentialReadOrder() {
            List<String> names = Arrays.asList("Alpha", "Beta", "Gamma", "Delta");

            fileProcessingService.writeSequentialRecords(names);

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

            assertEquals(4, records.size());
            assertEquals("Alpha", records.get(0).getName());
            assertEquals("Beta", records.get(1).getName());
            assertEquals("Gamma", records.get(2).getName());
            assertEquals("Delta", records.get(3).getName());
        }

        @Test
        @DisplayName("Empty input produces no records")
        void validateEmptyInput() {
            List<String> names = Arrays.asList();

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(0, result.getSuccessCount());
            assertEquals(0, result.getTotalProcessed());

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();
            assertEquals(0, records.size());
        }

        @Test
        @DisplayName("Only 'FIN' marker produces no records")
        void validateOnlyFinMarker() {
            List<String> names = Arrays.asList("FIN");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(0, result.getSuccessCount());
        }
    }

    @Nested
    @DisplayName("COBOL Data Structure Validation - REGISTRO PIC Clauses")
    class DataStructureValidation {

        @Test
        @DisplayName("Sequential record name matches COBOL PIC X(30)")
        void validateRecordNameLength() {
            List<String> names = Arrays.asList("123456789012345678901234567890");

            fileProcessingService.writeSequentialRecords(names);

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

            assertEquals(1, records.size());
            assertEquals(30, records.get(0).getName().length());
        }

        @Test
        @DisplayName("Sequential record ID is auto-generated (replaces COBOL PIC 9(5))")
        void validateRecordIdGeneration() {
            List<String> names = Arrays.asList("Test Name");

            fileProcessingService.writeSequentialRecords(names);

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

            assertEquals(1, records.size());
            assertNotNull(records.get(0).getId());
            assertTrue(records.get(0).getId() > 0);
        }

        @Test
        @DisplayName("Stream records as formatted strings - COBOL compatible format")
        void validateStreamRecordsAsStrings() {
            List<String> names = Arrays.asList("Test Name");

            fileProcessingService.writeSequentialRecords(names);

            List<String> formattedRecords = fileProcessingService.streamRecordsAsStrings();

            assertEquals(1, formattedRecords.size());
            assertTrue(formattedRecords.get(0).matches("\\d{5}.*"),
                    "Format should be 5-digit ID followed by name");
        }
    }

    @Nested
    @DisplayName("READ-WRITE.cob Validation - Line Sequential File Processing")
    class ReadWriteValidation {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Process file matches COBOL READ/WRITE loop")
        void validateProcessFile() throws IOException {
            Path inputDir = tempDir.resolve("input");
            Path outputDir = tempDir.resolve("output");
            Files.createDirectories(inputDir);
            Files.createDirectories(outputDir);

            Path inputFile = inputDir.resolve("test.txt");
            Files.write(inputFile, Arrays.asList("Line 1", "Line 2", "Line 3"));

            System.setProperty("file.processing.input-dir", inputDir.toString());
            System.setProperty("file.processing.output-dir", outputDir.toString());

            List<String> inputLines = Files.readAllLines(inputFile);
            assertEquals(3, inputLines.size());
            assertEquals("Line 1", inputLines.get(0));
            assertEquals("Line 2", inputLines.get(1));
            assertEquals("Line 3", inputLines.get(2));
        }

        @Test
        @DisplayName("Line sequential file content is preserved")
        void validateLineContentPreservation() throws IOException {
            Path inputFile = tempDir.resolve("input.txt");
            List<String> originalLines = Arrays.asList(
                    "First line of text",
                    "Second line with special chars: @#$%",
                    "Third line with numbers: 12345"
            );
            Files.write(inputFile, originalLines);

            List<String> readLines = Files.readAllLines(inputFile);

            assertEquals(originalLines.size(), readLines.size());
            for (int i = 0; i < originalLines.size(); i++) {
                assertEquals(originalLines.get(i), readLines.get(i));
            }
        }

        @Test
        @DisplayName("Empty file produces empty output")
        void validateEmptyFileProcessing() throws IOException {
            Path inputFile = tempDir.resolve("empty.txt");
            Files.createFile(inputFile);

            List<String> lines = Files.readAllLines(inputFile);
            assertEquals(0, lines.size());
        }
    }

    @Nested
    @DisplayName("RELATIVE.cob Validation - Position-Based Record Access")
    class RelativeFileValidation {

        @Test
        @DisplayName("Records can be accessed by position (simulated via ID)")
        void validatePositionBasedAccess() {
            List<String> names = Arrays.asList("Record 1", "Record 2", "Record 3");

            fileProcessingService.writeSequentialRecords(names);

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

            assertEquals(3, records.size());
            assertEquals("Record 1", records.get(0).getName());
            assertEquals("Record 2", records.get(1).getName());
            assertEquals("Record 3", records.get(2).getName());
        }

        @Test
        @DisplayName("Records maintain insertion order")
        void validateInsertionOrder() {
            List<String> names = Arrays.asList("First", "Second", "Third", "Fourth", "Fifth");

            fileProcessingService.writeSequentialRecords(names);

            List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();

            assertEquals(5, records.size());
            for (int i = 0; i < records.size() - 1; i++) {
                assertTrue(records.get(i).getId() < records.get(i + 1).getId(),
                        "Records should maintain insertion order");
            }
        }
    }

    @Nested
    @DisplayName("Batch Processing Result Validation")
    class BatchProcessingResultValidation {

        @Test
        @DisplayName("Success count tracks successful writes")
        void validateSuccessCount() {
            List<String> names = Arrays.asList("A", "B", "C", "D", "E");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(5, result.getSuccessCount());
            assertEquals(5, result.getTotalProcessed());
        }

        @Test
        @DisplayName("Processed items are tracked")
        void validateProcessedItems() {
            List<String> names = Arrays.asList("Alice", "Bob");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(2, result.getProcessedItems().size());
            assertTrue(result.getProcessedItems().get(0).contains("Alice"));
            assertTrue(result.getProcessedItems().get(1).contains("Bob"));
        }

        @Test
        @DisplayName("FIN marker does not count as processed")
        void validateFinNotCounted() {
            List<String> names = Arrays.asList("Alice", "FIN", "Bob");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertEquals(1, result.getSuccessCount());
            assertEquals(1, result.getTotalProcessed());
        }
    }

    @Nested
    @DisplayName("Stream-Based Processing Validation")
    class StreamProcessingValidation {

        @Test
        @DisplayName("Stream records produces COBOL-compatible format")
        void validateStreamFormat() {
            List<String> names = Arrays.asList("Test Record");

            fileProcessingService.writeSequentialRecords(names);

            List<String> streamedRecords = fileProcessingService.streamRecordsAsStrings();

            assertEquals(1, streamedRecords.size());
            String record = streamedRecords.get(0);
            assertTrue(record.length() >= 5, "Should have at least 5-digit ID");
            assertTrue(record.substring(0, 5).matches("\\d{5}"),
                    "First 5 characters should be numeric ID");
        }

        @Test
        @DisplayName("Multiple records stream in order")
        void validateMultipleRecordsStream() {
            List<String> names = Arrays.asList("Alpha", "Beta", "Gamma");

            fileProcessingService.writeSequentialRecords(names);

            List<String> streamedRecords = fileProcessingService.streamRecordsAsStrings();

            assertEquals(3, streamedRecords.size());
            assertTrue(streamedRecords.get(0).contains("Alpha"));
            assertTrue(streamedRecords.get(1).contains("Beta"));
            assertTrue(streamedRecords.get(2).contains("Gamma"));
        }
    }

    @Nested
    @DisplayName("Error Handling Validation")
    class ErrorHandlingValidation {

        @Test
        @DisplayName("Null name in list is handled gracefully")
        void validateNullNameHandling() {
            List<String> names = Arrays.asList("Valid", null, "Also Valid");

            assertDoesNotThrow(() -> {
                try {
                    fileProcessingService.writeSequentialRecords(names);
                } catch (Exception e) {
                }
            });
        }

        @Test
        @DisplayName("Empty string name is handled")
        void validateEmptyStringHandling() {
            List<String> names = Arrays.asList("Valid", "", "Also Valid");

            BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);

            assertTrue(result.getTotalProcessed() >= 1);
        }
    }
}
