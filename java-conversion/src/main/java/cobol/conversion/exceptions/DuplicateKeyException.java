package cobol.conversion.exceptions;

/**
 * Exception thrown when attempting to insert a record with a key that already exists.
 * 
 * COBOL to Java Mapping:
 * In COBOL indexed files, when you attempt to WRITE a record with a key that
 * already exists, the file status is set to "22" (FS-DUPLICATE).
 * 
 * Example COBOL code:
 *   WRITE PRODUCTS-RECORD
 *       INVALID KEY
 *           DISPLAY "ERROR: DUPLICATE CODE - " PROD-CODE
 *       NOT INVALID KEY
 *           DISPLAY "PRODUCT SUCCESSFULLY REGISTERED"
 *   END-WRITE.
 * 
 * In Java, we throw this exception instead of checking the INVALID KEY condition.
 */
public class DuplicateKeyException extends FileOperationException {
    
    private static final String COBOL_STATUS_CODE = "22";
    
    private final String duplicateKey;
    
    public DuplicateKeyException(String key) {
        super("Duplicate key: " + key, COBOL_STATUS_CODE);
        this.duplicateKey = key;
    }
    
    public DuplicateKeyException(String key, String message) {
        super(message, COBOL_STATUS_CODE);
        this.duplicateKey = key;
    }
    
    /**
     * Returns the key that caused the duplicate key error.
     */
    public String getDuplicateKey() {
        return duplicateKey;
    }
}
