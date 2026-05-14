package com.cobolintro.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * DAO for line-by-line file copy operations.
 * Replaces COBOL file copy logic in read-write.cbl
 * (reads from entrada.txt, writes to salida.txt).
 */
public class FileCopyDao {

    /**
     * Copies the contents of the input file to the output file, line by line.
     */
    public void copy(String inputPath, String outputPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
