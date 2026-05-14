package com.cobolintro.dao;

import com.cobolintro.model.SequentialRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for sequential file operations.
 * Replaces COBOL sequential file I/O in SEQUENTIAL-EXAMPLE.cbl.
 * Records are stored as pipe-delimited lines in {@code datos.dat}.
 */
public class SequentialFileDao {

    private static final String FILE_NAME = "datos.dat";
    private final Path filePath;

    public SequentialFileDao() {
        this.filePath = Paths.get(FILE_NAME);
    }

    public SequentialFileDao(String directory) {
        this.filePath = Paths.get(directory, FILE_NAME);
    }

    /**
     * Appends a record (id|name) to the sequential file.
     * Creates the file if it does not exist.
     */
    public void write(SequentialRecord r) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(r.getId() + "|" + r.getName());
            writer.newLine();
        }
    }

    /**
     * Reads all records from the sequential file, parsing each line as id|name.
     *
     * @return list of all sequential records in the file
     */
    public List<SequentialRecord> readAll() throws IOException {
        List<SequentialRecord> records = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return records;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                records.add(new SequentialRecord(id, name));
            }
        }
        return records;
    }
}
