package cobol.conversion.model;

import java.util.Objects;

/**
 * Java representation of a COBOL Relative file record.
 * 
 * COBOL to Java Mapping:
 * This class represents the COBOL record structure from Relative.cbl:
 * 
 *   01 RELATIVE-RECORD.
 *       05 IDNUM   PIC 9(5).    -> int id (5 digits, same as relative key)
 *       05 DA      PIC X(20).   -> String data (20 chars)
 * 
 * Relative File Characteristics:
 * In COBOL, relative files allow direct access to records by their
 * relative position (record number) in the file. The RELATIVE KEY
 * specifies which record position to read or write.
 * 
 * Example COBOL code:
 *   SELECT RELATIVE-FILE ASSIGN TO 'relativo.dat'
 *       ORGANIZATION IS RELATIVE
 *       ACCESS MODE IS DYNAMIC
 *       RELATIVE KEY IS RECORD-NUM.
 * 
 * In Java, we use an ArrayList where the index corresponds to the
 * COBOL relative key (record number). This allows O(1) direct access.
 */
public class RelativeRecord {
    
    private static final int DATA_LENGTH = 20;
    private static final int MAX_ID = 99999;
    
    private int id;
    private String data;
    
    public RelativeRecord() {
    }
    
    public RelativeRecord(int id, String data) {
        setId(id);
        setData(data);
    }
    
    public int getId() {
        return id;
    }
    
    /**
     * Sets the record ID (relative key).
     * In COBOL relative files, the ID corresponds to the record's
     * position in the file (1-based in COBOL, 0-based in Java).
     */
    public void setId(int id) {
        if (id < 0 || id > MAX_ID) {
            throw new IllegalArgumentException("ID must be between 0 and " + MAX_ID);
        }
        this.id = id;
    }
    
    public String getData() {
        return data;
    }
    
    /**
     * Sets the data field, padded or truncated to 20 characters.
     * In COBOL, PIC X(20) always stores exactly 20 characters.
     */
    public void setData(String data) {
        this.data = padOrTruncate(data, DATA_LENGTH);
    }
    
    /**
     * Pads or truncates a string to the specified length.
     * This mimics COBOL's fixed-length field behavior.
     */
    private String padOrTruncate(String value, int length) {
        if (value == null) {
            return String.format("%-" + length + "s", "");
        }
        if (value.length() > length) {
            return value.substring(0, length);
        }
        return String.format("%-" + length + "s", value);
    }
    
    /**
     * Converts the record to a fixed-length string representation,
     * similar to how COBOL stores records in a relative file.
     */
    public String toCobolRecord() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%05d", id));
        sb.append(padOrTruncate(data, DATA_LENGTH));
        return sb.toString();
    }
    
    /**
     * Creates a RelativeRecord from a fixed-length COBOL record string.
     */
    public static RelativeRecord fromCobolRecord(String record) {
        if (record == null || record.length() < 25) {
            throw new IllegalArgumentException("Invalid COBOL record format");
        }
        RelativeRecord rec = new RelativeRecord();
        rec.setId(Integer.parseInt(record.substring(0, 5).trim()));
        rec.setData(record.substring(5, 25).trim());
        return rec;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelativeRecord that = (RelativeRecord) o;
        return id == that.id && Objects.equals(data, that.data);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, data);
    }
    
    @Override
    public String toString() {
        return String.format("RelativeRecord{id=%d, data='%s'}",
                id,
                data != null ? data.trim() : null);
    }
}
