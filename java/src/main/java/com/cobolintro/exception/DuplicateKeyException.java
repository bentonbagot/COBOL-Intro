package com.cobolintro.exception;

/**
 * Exception indicating a duplicate key was encountered during a file/database operation.
 *
 * <p>Maps to COBOL FILE-STATUS code "22", which is raised when an attempt is made
 * to write a record with a key that already exists in an indexed file:
 * <pre>
 *   88 FS-DUPLICATE VALUE "22".
 * </pre>
 *
 * @see FileStatusException
 */
public class DuplicateKeyException extends FileStatusException {

    /**
     * Constructs a new {@code DuplicateKeyException} for the specified key value.
     *
     * @param key the duplicate key that caused the error
     */
    public DuplicateKeyException(String key) {
        super("22", "Duplicate key: " + key);
    }
}
