package cobol.conversion.model;

import java.util.Objects;

/**
 * Java representation of a COBOL Sequential file record.
 * 
 * COBOL to Java Mapping:
 * This class represents the COBOL record structure from SEQUENTIAL-EXAMPLE.cbl:
 * 
 *   01 RECORD.
 *       05 IDNUM     PIC 9(5).    -> int id (5 digits, auto-incremented)
 *       05 NAME      PIC X(30).   -> String name (30 chars)
 * 
 * Sequential File Characteristics:
 * In COBOL, sequential files are processed in order from beginning to end.
 * Records are written one after another and read in the same order.
 * There is no direct access by key or position - you must read through
 * all preceding records to reach a specific record.
 * 
 * In Java, we use ArrayList to maintain the sequential order and
 * Stream API for sequential processing.
 */
public class SequentialRecord {
    
    private static final int NAME_LENGTH = 30;
    private static final int MAX_ID = 99999;
    
    private int id;
    private String name;
    
    public SequentialRecord() {
    }
    
    public SequentialRecord(int id, String name) {
        setId(id);
        setName(name);
    }
    
    public int getId() {
        return id;
    }
    
    /**
     * Sets the record ID.
     * In COBOL, PIC 9(5) can store values from 0 to 99999.
     * The ID is typically auto-incremented when writing sequential records.
     */
    public void setId(int id) {
        if (id < 0 || id > MAX_ID) {
            throw new IllegalArgumentException("ID must be between 0 and " + MAX_ID);
        }
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name field, padded or truncated to 30 characters.
     * In COBOL, PIC X(30) always stores exactly 30 characters.
     */
    public void setName(String name) {
        this.name = padOrTruncate(name, NAME_LENGTH);
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
     * similar to how COBOL stores records in a sequential file.
     */
    public String toCobolRecord() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%05d", id));
        sb.append(padOrTruncate(name, NAME_LENGTH));
        return sb.toString();
    }
    
    /**
     * Creates a SequentialRecord from a fixed-length COBOL record string.
     */
    public static SequentialRecord fromCobolRecord(String record) {
        if (record == null || record.length() < 35) {
            throw new IllegalArgumentException("Invalid COBOL record format");
        }
        SequentialRecord rec = new SequentialRecord();
        rec.setId(Integer.parseInt(record.substring(0, 5).trim()));
        rec.setName(record.substring(5, 35).trim());
        return rec;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequentialRecord that = (SequentialRecord) o;
        return id == that.id && Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return String.format("SequentialRecord{id=%d, name='%s'}",
                id,
                name != null ? name.trim() : null);
    }
}
