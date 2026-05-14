package com.cobolintro.exception;

/**
 * Exception indicating that a requested record was not found in the file/database.
 *
 * <p>Maps to COBOL FILE-STATUS code "23", which is raised when a READ or START
 * operation fails to locate a record with the specified key:
 * <pre>
 *   88 FS-NOT-FOUND VALUE "23".
 * </pre>
 *
 * @see FileStatusException
 */
public class RecordNotFoundException extends FileStatusException {

    /**
     * Constructs a new {@code RecordNotFoundException} for the specified key value.
     *
     * @param key the key for which no record was found
     */
    public RecordNotFoundException(String key) {
        super("23", "Record not found: " + key);
    }
}
