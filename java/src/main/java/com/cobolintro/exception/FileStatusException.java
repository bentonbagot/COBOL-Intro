package com.cobolintro.exception;

/**
 * General file/database error exception mapping to COBOL FILE-STATUS codes.
 *
 * <p>In COBOL, FILE-STATUS is a two-character field (PIC XX) that indicates the
 * result of an I/O operation. Codes range from "00" (success) through "10"–"99"
 * for various error conditions:
 * <ul>
 *   <li>"00" — Successful completion</li>
 *   <li>"10" — End of file reached</li>
 *   <li>"22" — Duplicate key (see {@link DuplicateKeyException})</li>
 *   <li>"23" — Record not found (see {@link RecordNotFoundException})</li>
 *   <li>"35" — File does not exist</li>
 *   <li>"10" thru "99" — Other errors</li>
 * </ul>
 *
 * @see DuplicateKeyException
 * @see RecordNotFoundException
 */
public class FileStatusException extends RuntimeException {

    private final String statusCode;

    /**
     * Constructs a new {@code FileStatusException} with the specified COBOL
     * FILE-STATUS code and detail message.
     *
     * @param statusCode the two-character COBOL FILE-STATUS code (e.g. "22", "35")
     * @param message    a descriptive error message
     */
    public FileStatusException(String statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a new {@code FileStatusException} with the specified COBOL
     * FILE-STATUS code, detail message, and underlying cause.
     *
     * @param statusCode the two-character COBOL FILE-STATUS code (e.g. "22", "35")
     * @param message    a descriptive error message
     * @param cause      the underlying cause of this exception
     */
    public FileStatusException(String statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Returns the COBOL FILE-STATUS code associated with this exception.
     *
     * @return the two-character status code
     */
    public String getStatusCode() {
        return statusCode;
    }
}
