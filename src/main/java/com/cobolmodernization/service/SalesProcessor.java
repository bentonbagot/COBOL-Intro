package com.cobolmodernization.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.cobolmodernization.exception.SalesProcessingException;
import com.cobolmodernization.model.SalesRecord;

@Service
public class SalesProcessor {

    public void processSalesTransactions(Path salesFile) {
        try (Stream<String> lines = Files.lines(salesFile)) {
            lines.map(this::parseSalesRecord)
                 .forEach(this::processSalesRecord);
        } catch (IOException e) {
            throw new SalesProcessingException("Failed to process sales file", e);
        }
    }

    private SalesRecord parseSalesRecord(String line) {
        return SalesRecord.builder()
            .productCode(line.substring(0, 5))
            .quantity(Integer.parseInt(line.substring(5, 10).trim()))
            .build();
    }

    private void processSalesRecord(SalesRecord record) {
        System.out.println("Processing sale: " + record);
    }
}
