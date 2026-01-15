package com.cobolmodernization.exception;

/**
 * Exception thrown when attempting to create a record with a duplicate key.
 * Translated from COBOL file status 22 (duplicate key).
 * 
 * COBOL equivalent:
 *   WRITE PRODUCTS-RECORD
 *       INVALID KEY
 *           DISPLAY "ERROR: DUPLICATE CODE - " PROD-CODE
 */
public class DuplicateKeyException extends RuntimeException {

    private final String key;
    private final String entityType;

    public DuplicateKeyException(String entityType, String key) {
        super(String.format("Duplicate %s key: %s", entityType, key));
        this.entityType = entityType;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getEntityType() {
        return entityType;
    }
}
