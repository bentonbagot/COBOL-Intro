package cobol.conversion.handler;

import cobol.conversion.exceptions.FileOperationException;
import cobol.conversion.exceptions.RecordNotFoundException;
import cobol.conversion.model.RelativeRecord;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Java implementation of COBOL Relative File operations using ArrayList with index access.
 * 
 * COBOL to Java Mapping:
 * This class replaces COBOL relative file operations from Relative.cbl:
 * 
 *   SELECT RELATIVE-FILE ASSIGN TO 'relativo.dat'
 *       ORGANIZATION IS RELATIVE
 *       ACCESS MODE IS DYNAMIC
 *       RELATIVE KEY IS RECORD-NUM.
 * 
 * COBOL Relative File Characteristics:
 * - Records are accessed by their relative position (record number) in the file
 * - RELATIVE KEY specifies the record number for read/write operations
 * - Record numbers start at 1 in COBOL (we use 1-based indexing to match)
 * - ACCESS MODE IS DYNAMIC allows both random and sequential access
 * - Supports up to 99999 records (PIC 9(5) for RECORD-NUM)
 * 
 * Java Implementation:
 * - ArrayList<RelativeRecord> with sparse storage (null for empty slots)
 * - Index corresponds to COBOL relative key (1-based converted to 0-based)
 * - Direct O(1) access by position
 * 
 * COBOL File Operations Mapping:
 * - OPEN I-O      -> open()
 * - WRITE         -> write(position, record)
 * - READ          -> read(position)
 * - REWRITE       -> update(position, record)
 * - DELETE        -> delete(position)
 * - CLOSE         -> close()
 * - START         -> Not needed (direct access available)
 * - READ NEXT     -> readNext() for sequential access
 */
public class RelativeFileHandler {
    
    private static final int MAX_RECORDS = 99999;
    
    private final List<RelativeRecord> records;
    private final Path filePath;
    private int readPosition;
    private boolean isOpen;
    
    /**
     * Creates a new RelativeFileHandler.
     * Equivalent to COBOL: SELECT ... ASSIGN TO filename
     */
    public RelativeFileHandler(String filename) {
        this.records = new ArrayList<>(Collections.nCopies(MAX_RECORDS, null));
        this.filePath = Paths.get(filename);
        this.readPosition = 1;
        this.isOpen = false;
    }
    
    /**
     * Opens the relative file for I/O operations.
     * Equivalent to COBOL: OPEN I-O RELATIVE-FILE
     * 
     * If the file exists, loads existing records.
     */
    public void open() throws FileOperationException {
        if (isOpen) {
            return;
        }
        
        try {
            if (Files.exists(filePath)) {
                loadFromFile();
            }
            readPosition = 1;
            isOpen = true;
        } catch (IOException e) {
            throw new FileOperationException("Error opening file: " + filePath, "35", e);
        }
    }
    
    /**
     * Closes the relative file and persists data to disk.
     * Equivalent to COBOL: CLOSE RELATIVE-FILE
     */
    public void close() throws FileOperationException {
        if (!isOpen) {
            return;
        }
        
        try {
            saveToFile();
            isOpen = false;
        } catch (IOException e) {
            throw new FileOperationException("Error closing file: " + filePath, e);
        }
    }
    
    /**
     * Writes a record at the specified position (relative key).
     * Equivalent to COBOL:
     *   MOVE record-num TO RECORD-NUM
     *   MOVE data TO RELATIVE-RECORD
     *   WRITE RELATIVE-RECORD INVALID KEY
     *       DISPLAY "Error writing record"
     *   END-WRITE
     * 
     * Note: In COBOL, positions are 1-based. This method uses 1-based indexing
     * to match COBOL behavior.
     * 
     * @param position The relative key (1-based position)
     * @param data The data to store in the record
     * @throws FileOperationException if position is invalid or record already exists
     */
    public void write(int position, String data) throws FileOperationException {
        checkOpen();
        validatePosition(position);
        
        int index = position - 1;
        if (records.get(index) != null) {
            throw new FileOperationException("Record already exists at position: " + position, "22");
        }
        
        RelativeRecord record = new RelativeRecord(position, data);
        records.set(index, record);
    }
    
    /**
     * Writes or overwrites a record at the specified position.
     * This combines WRITE and REWRITE functionality for convenience.
     */
    public void writeOrUpdate(int position, String data) throws FileOperationException {
        checkOpen();
        validatePosition(position);
        
        int index = position - 1;
        RelativeRecord record = new RelativeRecord(position, data);
        records.set(index, record);
    }
    
    /**
     * Reads a record at the specified position (relative key).
     * Equivalent to COBOL:
     *   MOVE search-position TO RECORD-NUM
     *   READ RELATIVE-FILE
     *       INVALID KEY DISPLAY "Record not found"
     *       NOT INVALID KEY DISPLAY record-data
     *   END-READ
     * 
     * @param position The relative key (1-based position)
     * @return The record at the specified position
     * @throws RecordNotFoundException if no record exists at the position (FS-NOT-FOUND "23")
     */
    public RelativeRecord read(int position) throws RecordNotFoundException, FileOperationException {
        checkOpen();
        validatePosition(position);
        
        int index = position - 1;
        RelativeRecord record = records.get(index);
        if (record == null) {
            throw new RecordNotFoundException(position);
        }
        
        return record;
    }
    
    /**
     * Reads the next non-null record in sequence.
     * Equivalent to COBOL: READ RELATIVE-FILE NEXT
     * 
     * Skips empty positions and returns the next available record.
     * 
     * @return Optional containing the next record, or empty if at end of file
     */
    public Optional<RelativeRecord> readNext() throws FileOperationException {
        checkOpen();
        
        while (readPosition <= MAX_RECORDS) {
            int index = readPosition - 1;
            readPosition++;
            
            RelativeRecord record = records.get(index);
            if (record != null) {
                return Optional.of(record);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Updates an existing record at the specified position.
     * Equivalent to COBOL: REWRITE RELATIVE-RECORD
     * 
     * @param position The relative key (1-based position)
     * @param data The new data for the record
     * @throws RecordNotFoundException if no record exists at the position
     */
    public void update(int position, String data) throws RecordNotFoundException, FileOperationException {
        checkOpen();
        validatePosition(position);
        
        int index = position - 1;
        if (records.get(index) == null) {
            throw new RecordNotFoundException(position);
        }
        
        RelativeRecord record = new RelativeRecord(position, data);
        records.set(index, record);
    }
    
    /**
     * Deletes a record at the specified position.
     * Equivalent to COBOL: DELETE RELATIVE-FILE
     * 
     * @param position The relative key (1-based position)
     * @throws RecordNotFoundException if no record exists at the position
     */
    public void delete(int position) throws RecordNotFoundException, FileOperationException {
        checkOpen();
        validatePosition(position);
        
        int index = position - 1;
        if (records.get(index) == null) {
            throw new RecordNotFoundException(position);
        }
        
        records.set(index, null);
    }
    
    /**
     * Checks if a record exists at the specified position.
     */
    public boolean exists(int position) throws FileOperationException {
        checkOpen();
        if (position < 1 || position > MAX_RECORDS) {
            return false;
        }
        return records.get(position - 1) != null;
    }
    
    /**
     * Resets the sequential read position to the beginning.
     */
    public void resetReadPosition() throws FileOperationException {
        checkOpen();
        readPosition = 1;
    }
    
    /**
     * Sets the sequential read position to a specific location.
     * Equivalent to COBOL: START RELATIVE-FILE KEY IS = position
     */
    public void setReadPosition(int position) throws FileOperationException {
        checkOpen();
        validatePosition(position);
        readPosition = position;
    }
    
    /**
     * Returns all non-null records.
     */
    public List<RelativeRecord> getAll() throws FileOperationException {
        checkOpen();
        return records.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns the count of non-null records.
     */
    public int size() throws FileOperationException {
        checkOpen();
        return (int) records.stream()
                .filter(Objects::nonNull)
                .count();
    }
    
    /**
     * Returns the maximum allowed records (99999).
     */
    public int getMaxRecords() {
        return MAX_RECORDS;
    }
    
    private void checkOpen() throws FileOperationException {
        if (!isOpen) {
            throw new FileOperationException("File is not open", "47");
        }
    }
    
    private void validatePosition(int position) throws FileOperationException {
        if (position < 1 || position > MAX_RECORDS) {
            throw new FileOperationException(
                    "Invalid position: " + position + ". Must be between 1 and " + MAX_RECORDS,
                    "24");
        }
    }
    
    /**
     * Loads data from the file into the ArrayList.
     * Records are stored with their position (relative key) embedded.
     */
    private void loadFromFile() throws IOException {
        for (int i = 0; i < MAX_RECORDS; i++) {
            records.set(i, null);
        }
        
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            if (line.length() >= 25) {
                try {
                    RelativeRecord record = RelativeRecord.fromCobolRecord(line);
                    int index = record.getId() - 1;
                    if (index >= 0 && index < MAX_RECORDS) {
                        records.set(index, record);
                    }
                } catch (Exception e) {
                    // Skip invalid records
                }
            }
        }
    }
    
    /**
     * Saves the ArrayList data to the file in COBOL record format.
     * Only non-null records are written.
     */
    private void saveToFile() throws IOException {
        List<String> lines = records.stream()
                .filter(Objects::nonNull)
                .map(RelativeRecord::toCobolRecord)
                .collect(Collectors.toList());
        
        Files.write(filePath, lines);
    }
}
