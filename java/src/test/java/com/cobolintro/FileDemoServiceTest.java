package com.cobolintro;

import com.cobolintro.dao.FileCopyDao;
import com.cobolintro.dao.RelativeFileDao;
import com.cobolintro.dao.SequentialFileDao;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.RelativeRecord;
import com.cobolintro.model.SequentialRecord;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileDemoServiceTest {

    private static final String TEST_DIR = "test_file_demo";
    private SequentialFileDao sequentialFileDao;
    private RelativeFileDao relativeFileDao;
    private FileCopyDao fileCopyDao;

    @BeforeEach
    void setUp() {
        new File(TEST_DIR).mkdirs();
        // Clean up any leftover test files
        new File(TEST_DIR, "datos.dat").delete();
        new File(TEST_DIR, "relativo.dat").delete();

        sequentialFileDao = new SequentialFileDao(TEST_DIR);
        relativeFileDao = new RelativeFileDao(TEST_DIR);
        fileCopyDao = new FileCopyDao();
    }

    @AfterEach
    void tearDown() {
        new File(TEST_DIR, "datos.dat").delete();
        new File(TEST_DIR, "relativo.dat").delete();
        new File(TEST_DIR, "test_input.txt").delete();
        new File(TEST_DIR, "test_output.txt").delete();
        new File(TEST_DIR).delete();
    }

    @Test
    @DisplayName("testSequentialWriteAndRead — write records, readAll, verify count and content")
    void testSequentialWriteAndRead() throws IOException {
        sequentialFileDao.write(new SequentialRecord(1, "Alice"));
        sequentialFileDao.write(new SequentialRecord(2, "Bob"));
        sequentialFileDao.write(new SequentialRecord(3, "Carol"));

        List<SequentialRecord> records = sequentialFileDao.readAll();
        assertEquals(3, records.size());
        assertEquals(1, records.get(0).getId());
        assertEquals("Alice", records.get(0).getName());
        assertEquals(2, records.get(1).getId());
        assertEquals("Bob", records.get(1).getName());
        assertEquals(3, records.get(2).getId());
        assertEquals("Carol", records.get(2).getName());
    }

    @Test
    @DisplayName("testRelativeAccess — write at positions, read back, verify correct records")
    void testRelativeAccess() throws IOException {
        relativeFileDao.write(1, new RelativeRecord(101, "Data at pos 1"));
        relativeFileDao.write(5, new RelativeRecord(505, "Data at pos 5"));
        relativeFileDao.write(10, new RelativeRecord(1010, "Data at pos 10"));

        RelativeRecord r1 = relativeFileDao.read(1);
        assertEquals(101, r1.getId());
        assertEquals("Data at pos 1", r1.getData());

        RelativeRecord r5 = relativeFileDao.read(5);
        assertEquals(505, r5.getId());
        assertEquals("Data at pos 5", r5.getData());

        RelativeRecord r10 = relativeFileDao.read(10);
        assertEquals(1010, r10.getId());
        assertEquals("Data at pos 10", r10.getData());
    }

    @Test
    @DisplayName("testRelativeAccessNotFound — read non-existent position, assert RecordNotFoundException")
    void testRelativeAccessNotFound() {
        assertThrows(RecordNotFoundException.class, () -> relativeFileDao.read(99));
    }

    @Test
    @DisplayName("testFileCopy — create a temp input file, copy it, verify output matches input")
    void testFileCopy() throws IOException {
        Path inputPath = Path.of(TEST_DIR, "test_input.txt");
        Path outputPath = Path.of(TEST_DIR, "test_output.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(inputPath)) {
            writer.write("Line 1");
            writer.newLine();
            writer.write("Line 2");
            writer.newLine();
            writer.write("Line 3");
            writer.newLine();
        }

        fileCopyDao.copy(inputPath.toString(), outputPath.toString());

        assertTrue(outputPath.toFile().exists());

        List<String> inputLines = Files.readAllLines(inputPath);
        List<String> outputLines = Files.readAllLines(outputPath);
        assertEquals(inputLines.size(), outputLines.size());
        for (int i = 0; i < inputLines.size(); i++) {
            assertEquals(inputLines.get(i), outputLines.get(i));
        }
    }
}
