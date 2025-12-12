package cobol.conversion.exceptions;

/**
 * Base exception class for file operations.
 * 
 * COBOL to Java Mapping:
 * In COBOL, file operations return status codes in a FILE STATUS variable (PIC XX).
 * Common status codes include:
 *   - "00" = Success (FS-OK)
 *   - "10" = End of file (FS-END-OF-FILE)
 *   - "22" = Duplicate key (FS-DUPLICATE)
 *   - "23" = Record not found (FS-NOT-FOUND)
 *   - "35" = File not found (FS-FILE-NOT-EXIST)
 *   - "30" = Invalid organization (FS-INVALID-ORG)
 * 
 * In Java, we use exceptions to handle these error conditions instead of
 * checking status codes after each operation.
 */
public class FileOperationException extends Exception {
    
    private final String cobolStatusCode;
    
    public FileOperationException(String message) {
        super(message);
        this.cobolStatusCode = "99";
    }
    
    public FileOperationException(String message, String cobolStatusCode) {
        super(message);
        this.cobolStatusCode = cobolStatusCode;
    }
    
    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
        this.cobolStatusCode = "99";
    }
    
    public FileOperationException(String message, String cobolStatusCode, Throwable cause) {
        super(message, cause);
        this.cobolStatusCode = cobolStatusCode;
    }
    
    /**
     * Returns the equivalent COBOL file status code.
     * This helps in understanding the mapping between Java exceptions
     * and COBOL file status codes.
     */
    public String getCobolStatusCode() {
        return cobolStatusCode;
    }
}
