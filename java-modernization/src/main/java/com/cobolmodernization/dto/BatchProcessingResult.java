package com.cobolmodernization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object for batch processing operations.
 * Tracks success/failure counts similar to COBOL counter variables.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchProcessingResult {

    @Builder.Default
    private int totalProcessed = 0;

    @Builder.Default
    private int successCount = 0;

    @Builder.Default
    private int failureCount = 0;

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder.Default
    private List<String> processedItems = new ArrayList<>();

    public void incrementSuccess() {
        successCount++;
        totalProcessed++;
    }

    public void incrementFailure(String errorMessage) {
        failureCount++;
        totalProcessed++;
        errors.add(errorMessage);
    }

    public void addProcessedItem(String item) {
        processedItems.add(item);
    }
}
