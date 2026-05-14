package com.cobolintro.dao;

import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.RelativeRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO for relative file operations using text-based storage.
 * Replaces COBOL relative file I/O in Relative.cbl.
 * Records are stored as pipe-delimited lines (position|id|data) in {@code relativo.dat}.
 */
public class RelativeFileDao {

    private static final String FILE_NAME = "relativo.dat";
    private final Path filePath;

    public RelativeFileDao() {
        this.filePath = Paths.get(FILE_NAME);
    }

    public RelativeFileDao(String directory) {
        this.filePath = Paths.get(directory, FILE_NAME);
    }

    /**
     * Stores a record at the given position, then writes all entries to file.
     */
    public void write(int position, RelativeRecord r) throws IOException {
        Map<Integer, RelativeRecord> map = loadAll();
        map.put(position, r);
        saveAll(map);
    }

    /**
     * Reads the record at the given position from file.
     *
     * @throws RecordNotFoundException if no record exists at the specified position
     */
    public RelativeRecord read(int position) throws IOException {
        Map<Integer, RelativeRecord> map = loadAll();
        RelativeRecord record = map.get(position);
        if (record == null) {
            throw new RecordNotFoundException(String.valueOf(position));
        }
        return record;
    }

    private Map<Integer, RelativeRecord> loadAll() throws IOException {
        Map<Integer, RelativeRecord> map = new LinkedHashMap<>();
        if (!Files.exists(filePath)) {
            return map;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                int position = Integer.parseInt(parts[0]);
                int id = Integer.parseInt(parts[1]);
                String data = parts[2];
                map.put(position, new RelativeRecord(id, data));
            }
        }
        return map;
    }

    private void saveAll(Map<Integer, RelativeRecord> map) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Map.Entry<Integer, RelativeRecord> entry : map.entrySet()) {
                RelativeRecord r = entry.getValue();
                writer.write(entry.getKey() + "|" + r.getId() + "|" + r.getData());
                writer.newLine();
            }
        }
    }
}
