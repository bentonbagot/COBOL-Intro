package com.cobolmodernization.exception;

/**
 * Exception thrown when a record is not found.
 * Translated from COBOL file status 23 (record not found).
 * 
 * COBOL equivalent:
 *   READ ARCHIVO-CLIENTES
 *       INVALID KEY DISPLAY "No encontrado"
 */
public class RecordNotFoundException extends RuntimeException {

    private final String key;
    private final String entityType;

    public RecordNotFoundException(String entityType, String key) {
        super(String.format("%s not found with key: %s", entityType, key));
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
