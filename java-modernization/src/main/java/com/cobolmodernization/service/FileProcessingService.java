package com.cobolmodernization.service;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.entity.SequentialRecord;
import com.cobolmodernization.repository.SequentialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for File Processing operations.
 * Translates business logic from COBOL programs:
 * - EJEMPLO-SECUENCIAL.cbl / SEQUENTIAL-EXAMPLE.cbl - Sequential file writing with auto-increment IDs
 * - lee-escribe.cbl / read-write.cbl - Line sequential text file processing
 * 
 * Modern Implementation:
 * - Uses Java Streams for sequential processing
 * - Uses JPA for database persistence instead of file-based storage
 * - Supports both database and file-based operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {

    private final SequentialRecordRepository sequentialRecordRepository;

    @Value("${file.processing.input-dir:./input}")
    private String inputDir;

    @Value("${file.processing.output-dir:./output}")
    private String outputDir;

    /**
     * Write sequential records to database.
     * Translated from COBOL EJEMPLO-SECUENCIAL.cbl:
     *   MOVE ID-ACTUAL TO IDNUM
     *   MOVE NOMBRE-ACTUAL TO NOMBRE
     *   WRITE REGISTRO
     *   ADD 1 TO ID-ACTUAL
     * 
     * @param names list of names to write
     * @return batch processing result
     */
    @Transactional
    public BatchProcessingResult writeSequentialRecords(List<String> names) {
        log.info("Writing sequential records...");
        BatchProcessingResult result = new BatchProcessingResult();

        for (String name : names) {
            if ("FIN".equalsIgnoreCase(name)) {
                log.info("End marker received, stopping");
                break;
            }

            try {
                SequentialRecord record = SequentialRecord.builder()
                        .name(name)
                        .build();
                SequentialRecord saved = sequentialRecordRepository.save(record);
                result.incrementSuccess();
                result.addProcessedItem(String.format("ID: %d, Name: %s", saved.getId(), saved.getName()));
                log.info("Record written: ID={}, Name={}", saved.getId(), saved.getName());
            } catch (Exception e) {
                log.error("Error writing record: {}", e.getMessage());
                result.incrementFailure(String.format("Failed to write: %s - %s", name, e.getMessage()));
            }
        }

        log.info("Sequential write complete. Total: {}", result.getSuccessCount());
        return result;
    }

    /**
     * Read all sequential records.
     * 
     * @return list of all records in sequential order
     */
    @Transactional(readOnly = true)
    public List<SequentialRecord> readAllSequentialRecords() {
        log.info("Reading all sequential records...");
        return sequentialRecordRepository.findAllByOrderByIdAsc();
    }

    /**
     * Process file: read from input and write to output.
     * Translated from COBOL lee-escribe.cbl / read-write.cbl:
     *   READ ARCHIVO-ENTRADA
     *   MOVE REGISTRO-ENTRADA TO REGISTRO-SALIDA
     *   WRITE REGISTRO-SALIDA
     * 
     * @param inputFileName input file name
     * @param outputFileName output file name
     * @return batch processing result
     */
    public BatchProcessingResult processFile(String inputFileName, String outputFileName) {
        log.info("Processing file: {} -> {}", inputFileName, outputFileName);
        BatchProcessingResult result = new BatchProcessingResult();

        Path inputPath = Paths.get(inputDir, inputFileName);
        Path outputPath = Paths.get(outputDir, outputFileName);

        try {
            Files.createDirectories(outputPath.getParent());

            try (BufferedReader reader = Files.newBufferedReader(inputPath);
                 BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                    result.incrementSuccess();
                    result.addProcessedItem(line);
                }
            }

            log.info("Process completed. Lines processed: {}", result.getSuccessCount());
        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage());
            result.incrementFailure(String.format("File processing error: %s", e.getMessage()));
        }

        return result;
    }

    /**
     * Process file using Java Streams.
     * Modern streaming approach for file processing.
     * 
     * @param inputFileName input file name
     * @param outputFileName output file name
     * @return batch processing result
     */
    public BatchProcessingResult processFileWithStreams(String inputFileName, String outputFileName) {
        log.info("Processing file with streams: {} -> {}", inputFileName, outputFileName);
        BatchProcessingResult result = new BatchProcessingResult();

        Path inputPath = Paths.get(inputDir, inputFileName);
        Path outputPath = Paths.get(outputDir, outputFileName);

        try {
            Files.createDirectories(outputPath.getParent());

            try (Stream<String> lines = Files.lines(inputPath);
                 BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

                lines.forEach(line -> {
                    try {
                        writer.write(line);
                        writer.newLine();
                        result.incrementSuccess();
                        result.addProcessedItem(line);
                    } catch (IOException e) {
                        result.incrementFailure(String.format("Error writing line: %s", e.getMessage()));
                    }
                });
            }

            log.info("Stream processing completed. Lines processed: {}", result.getSuccessCount());
        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage());
            result.incrementFailure(String.format("File processing error: %s", e.getMessage()));
        }

        return result;
    }

    /**
     * Stream sequential records from database.
     * Modern streaming approach for batch processing.
     * 
     * @return stream of records
     */
    @Transactional(readOnly = true)
    public List<String> streamRecordsAsStrings() {
        log.info("Streaming sequential records...");
        return sequentialRecordRepository.findAllByOrderByIdAsc().stream()
                .map(record -> String.format("%05d%s", record.getId(), record.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Export sequential records to file.
     * Combines database reading with file writing.
     * 
     * @param outputFileName output file name
     * @return batch processing result
     */
    public BatchProcessingResult exportRecordsToFile(String outputFileName) {
        log.info("Exporting records to file: {}", outputFileName);
        BatchProcessingResult result = new BatchProcessingResult();

        Path outputPath = Paths.get(outputDir, outputFileName);

        try {
            Files.createDirectories(outputPath.getParent());

            List<SequentialRecord> records = sequentialRecordRepository.findAllByOrderByIdAsc();

            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                for (SequentialRecord record : records) {
                    String line = String.format("%05d%-30s", record.getId(), record.getName());
                    writer.write(line);
                    writer.newLine();
                    result.incrementSuccess();
                    result.addProcessedItem(line.trim());
                }
            }

            log.info("Export completed. Records exported: {}", result.getSuccessCount());
        } catch (IOException e) {
            log.error("Error exporting records: {}", e.getMessage());
            result.incrementFailure(String.format("Export error: %s", e.getMessage()));
        }

        return result;
    }
}
