package com.cobolmodernization.component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.cobolmodernization.exception.FileProcessingException;

@Component
public class TextFileProcessor {

    public void processTextFile(Path input, Path output) {
        try (BufferedReader reader = Files.newBufferedReader(input);
             BufferedWriter writer = Files.newBufferedWriter(output)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = processLine(line);
                writer.write(processedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileProcessingException("Text processing failed", e);
        }
    }

    private String processLine(String line) {
        return line;
    }
}
