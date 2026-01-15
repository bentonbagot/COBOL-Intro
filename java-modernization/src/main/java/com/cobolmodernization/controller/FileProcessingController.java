package com.cobolmodernization.controller;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.entity.SequentialRecord;
import com.cobolmodernization.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for File Processing operations.
 * Transforms COBOL sequential file processing into REST API endpoints.
 * 
 * COBOL Program Mapping:
 * - EJEMPLO-SECUENCIAL.cbl / SEQUENTIAL-EXAMPLE.cbl -> POST /api/files/sequential
 * - lee-escribe.cbl / read-write.cbl -> POST /api/files/process
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileProcessingController {

    private final FileProcessingService fileProcessingService;

    /**
     * Write sequential records.
     * Translated from COBOL EJEMPLO-SECUENCIAL.cbl.
     * Writes records with auto-increment IDs.
     */
    @PostMapping("/sequential")
    public ResponseEntity<BatchProcessingResult> writeSequentialRecords(@RequestBody List<String> names) {
        BatchProcessingResult result = fileProcessingService.writeSequentialRecords(names);
        return ResponseEntity.ok(result);
    }

    /**
     * Read all sequential records.
     */
    @GetMapping("/sequential")
    public ResponseEntity<List<SequentialRecord>> readSequentialRecords() {
        List<SequentialRecord> records = fileProcessingService.readAllSequentialRecords();
        return ResponseEntity.ok(records);
    }

    /**
     * Process file: read from input and write to output.
     * Translated from COBOL lee-escribe.cbl / read-write.cbl.
     */
    @PostMapping("/process")
    public ResponseEntity<BatchProcessingResult> processFile(
            @RequestParam String inputFile,
            @RequestParam String outputFile) {
        BatchProcessingResult result = fileProcessingService.processFile(inputFile, outputFile);
        return ResponseEntity.ok(result);
    }

    /**
     * Process file using Java Streams.
     * Modern streaming approach for file processing.
     */
    @PostMapping("/process/stream")
    public ResponseEntity<BatchProcessingResult> processFileWithStreams(
            @RequestParam String inputFile,
            @RequestParam String outputFile) {
        BatchProcessingResult result = fileProcessingService.processFileWithStreams(inputFile, outputFile);
        return ResponseEntity.ok(result);
    }

    /**
     * Stream records as formatted strings.
     * Returns records in COBOL-compatible format (ID + Name).
     */
    @GetMapping("/sequential/formatted")
    public ResponseEntity<List<String>> getFormattedRecords() {
        List<String> records = fileProcessingService.streamRecordsAsStrings();
        return ResponseEntity.ok(records);
    }

    /**
     * Export sequential records to file.
     */
    @PostMapping("/sequential/export")
    public ResponseEntity<BatchProcessingResult> exportRecords(@RequestParam String outputFile) {
        BatchProcessingResult result = fileProcessingService.exportRecordsToFile(outputFile);
        return ResponseEntity.ok(result);
    }
}
