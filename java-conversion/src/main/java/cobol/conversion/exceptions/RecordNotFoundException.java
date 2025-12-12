package cobol.conversion.exceptions;

/**
 * Exception thrown when a record cannot be found by its key or index.
 * 
 * COBOL to Java Mapping:
 * In COBOL indexed files, when you attempt to READ a record with a key that
 * doesn't exist, the file status is set to "23" (FS-NOT-FOUND).
 * 
 * Example COBOL code:
 *   MOVE CLAVE-BUSQUEDA TO CLAVE-CLIENTE
 *   READ ARCHIVO-CLIENTES
 *       INVALID KEY DISPLAY "No encontrado"
 *       NOT INVALID DISPLAY NOMBRE-CLIENTE " - " TELEFONO
 *   END-READ.
 * 
 * In Java, we throw this exception instead of checking the INVALID KEY condition.
 */
public class RecordNotFoundException extends FileOperationException {
    
    private static final String COBOL_STATUS_CODE = "23";
    
    private final String searchKey;
    private final Integer searchIndex;
    
    public RecordNotFoundException(String key) {
        super("Record not found for key: " + key, COBOL_STATUS_CODE);
        this.searchKey = key;
        this.searchIndex = null;
    }
    
    public RecordNotFoundException(int index) {
        super("Record not found at index: " + index, COBOL_STATUS_CODE);
        this.searchKey = null;
        this.searchIndex = index;
    }
    
    public RecordNotFoundException(String key, String message) {
        super(message, COBOL_STATUS_CODE);
        this.searchKey = key;
        this.searchIndex = null;
    }
    
    /**
     * Returns the key that was searched for (for indexed files).
     */
    public String getSearchKey() {
        return searchKey;
    }
    
    /**
     * Returns the index that was searched for (for relative files).
     */
    public Integer getSearchIndex() {
        return searchIndex;
    }
}
