package com.cobolintro.service;

import com.cobolintro.dao.FileCopyDao;
import com.cobolintro.dao.RelativeFileDao;
import com.cobolintro.dao.SequentialFileDao;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.RelativeRecord;
import com.cobolintro.model.SequentialRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service demonstrating file operations and arithmetic,
 * replacing several COBOL programs with Java equivalents.
 */
public class FileDemoService {

    private final SequentialFileDao sequentialFileDao;
    private final RelativeFileDao relativeFileDao;
    private final FileCopyDao fileCopyDao;

    public FileDemoService(SequentialFileDao sequentialFileDao,
                           RelativeFileDao relativeFileDao,
                           FileCopyDao fileCopyDao) {
        this.sequentialFileDao = sequentialFileDao;
        this.relativeFileDao = relativeFileDao;
        this.fileCopyDao = fileCopyDao;
    }

    /**
     * Writes 5 sequential records with auto-increment IDs and reads them back.
     * Replaces SEQUENTIAL-EXAMPLE.cbl.
     */
    public void sequentialDemo() throws IOException {
        System.out.println("=== Sequential File Demo ===");

        for (int i = 1; i <= 5; i++) {
            SequentialRecord record = new SequentialRecord(i, "Record " + i);
            sequentialFileDao.write(record);
            System.out.println("Wrote: " + record);
        }

        System.out.println("\nReading all sequential records:");
        List<SequentialRecord> records = sequentialFileDao.readAll();
        for (SequentialRecord record : records) {
            System.out.println("  " + record);
        }
        System.out.println();
    }

    /**
     * Writes records at specific positions and reads them back by position.
     * Handles RecordNotFoundException for non-existent positions.
     * Replaces Relative.cbl.
     */
    public void relativeDemo() throws IOException {
        System.out.println("=== Relative File Demo ===");

        int[] positions = {1, 5, 10};
        for (int pos : positions) {
            RelativeRecord record = new RelativeRecord(pos, "Data at position " + pos);
            relativeFileDao.write(pos, record);
            System.out.println("Wrote at position " + pos + ": " + record);
        }

        System.out.println("\nReading records by position:");
        int[] readPositions = {1, 3, 5, 7, 10};
        for (int pos : readPositions) {
            try {
                RelativeRecord record = relativeFileDao.read(pos);
                System.out.println("  Position " + pos + ": " + record);
            } catch (RecordNotFoundException e) {
                System.out.println("  Position " + pos + ": " + e.getMessage());
            }
        }
        System.out.println();
    }

    /**
     * Copies contents from inputFile to outputFile and prints a confirmation.
     * Replaces read-write.cbl.
     */
    public void readWriteDemo(String inputFile, String outputFile) throws IOException {
        System.out.println("=== Read/Write (File Copy) Demo ===");
        fileCopyDao.copy(inputFile, outputFile);
        System.out.println("Copied '" + inputFile + "' to '" + outputFile + "' successfully.");
        System.out.println();
    }

    /**
     * Demonstrates ADD, SUBTRACT, MULTIPLY, DIVIDE, and COMPUTE equivalents
     * from aritmeti.cbl using BigDecimal for all arithmetic.
     */
    public void arithmeticDemo() {
        System.out.println("=== Arithmetic Demo ===");

        BigDecimal numA = new BigDecimal("100");
        BigDecimal numB = new BigDecimal("50");
        BigDecimal result;

        // ADD
        result = numA.add(numB);
        System.out.println("ADD:      " + numA + " + " + numB + " = " + result);

        // SUBTRACT
        result = numA.subtract(numB);
        System.out.println("SUBTRACT: " + numA + " - " + numB + " = " + result);

        // MULTIPLY
        result = numA.multiply(numB);
        System.out.println("MULTIPLY: " + numA + " * " + numB + " = " + result);

        // DIVIDE
        result = numA.divide(numB, 2, RoundingMode.HALF_UP);
        System.out.println("DIVIDE:   " + numA + " / " + numB + " = " + result);

        // COMPUTE: (numA + numB) * numA - numB
        result = numA.add(numB).multiply(numA).subtract(numB);
        System.out.println("COMPUTE:  (" + numA + " + " + numB + ") * " + numA + " - " + numB + " = " + result);

        System.out.println();
    }
}
