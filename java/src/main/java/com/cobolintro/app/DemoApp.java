package com.cobolintro.app;

import com.cobolintro.dao.FileCopyDao;
import com.cobolintro.dao.RelativeFileDao;
import com.cobolintro.dao.SequentialFileDao;
import com.cobolintro.service.FileDemoService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Runs all file demos and arithmetic demo with console output.
 * Replaces the demo/utility COBOL programs.
 */
public class DemoApp {

    public static void main(String[] args) {
        SequentialFileDao sequentialFileDao = new SequentialFileDao();
        RelativeFileDao relativeFileDao = new RelativeFileDao();
        FileCopyDao fileCopyDao = new FileCopyDao();
        FileDemoService fileDemoService = new FileDemoService(sequentialFileDao, relativeFileDao, fileCopyDao);

        try {
            // 1. Sequential File Demo
            System.out.println("=== Sequential File Demo ===");
            fileDemoService.sequentialDemo();

            // 2. Relative File Demo
            System.out.println("=== Relative File Demo ===");
            fileDemoService.relativeDemo();

            // 3. File Copy Demo
            System.out.println("=== File Copy Demo ===");
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("demo_input.txt"))) {
                writer.write("Hello from the COBOL-to-Java migration!");
                writer.newLine();
                writer.write("This is a sample input file.");
                writer.newLine();
                writer.write("Line three of demo data.");
                writer.newLine();
            }
            fileDemoService.readWriteDemo("demo_input.txt", "demo_output.txt");
            System.out.println("File copy completed: demo_input.txt -> demo_output.txt");

            // 4. Arithmetic Demo
            System.out.println("=== Arithmetic Demo ===");
            fileDemoService.arithmeticDemo();

        } catch (IOException e) {
            System.err.println("Error during demo execution: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("All demos completed successfully!");
    }
}
