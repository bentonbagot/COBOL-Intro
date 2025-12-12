package cobol.conversion.handler;

import cobol.conversion.exceptions.FileOperationException;
import cobol.conversion.model.SequentialRecord;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java implementation of COBOL Sequential File operations using ArrayList and Streams.
 * 
 * COBOL to Java Mapping:
 * This class replaces COBOL sequential file operations from SEQUENTIAL-EXAMPLE.cbl:
 * 
 *   SELECT SEQUENTIAL-FILE ASSIGN TO 'datos.dat'
 *       ORGANIZATION IS SEQUENTIAL.
 * 
 * COBOL Sequential File Characteristics:
 * - Records are stored and accessed in the order they were written
 * - No direct access by key or position
 * - Must read through all preceding records to reach a specific record
 * - Typically used for batch processing, reports, and data transfer
 * - Records are written with auto-incrementing IDs
 * 
 * Java Implementation:
 * - ArrayList<SequentialRecord> maintains insertion order
 * - Stream API provides functional-style sequential processing
 * - Auto-incrementing ID mimics COBOL's CURRENT-ID counter
 * 
 * COBOL File Operations Mapping:
 * - OPEN OUTPUT    -> openForWrite()
 * - OPEN INPUT     -> openForRead()
 * - OPEN EXTEND    -> openForAppend()
 * - WRITE          -> write()
 * - READ           -> read() / readNext()
 * - CLOSE          -> close()
 */
public class SequentialFileHandler {
    
    private final List<SequentialRecord> records;
    private final Path filePath;
    private int currentId;
    private int readPosition;
    private boolean isOpen;
    private OpenMode openMode;
    
    /**
     * Represents the mode in which the file is opened.
     * COBOL equivalent: OPEN INPUT / OUTPUT / EXTEND
     */
    public enum OpenMode {
        INPUT,      // Read-only access
        OUTPUT,     // Write-only, creates new file
        EXTEND      // Append to existing file
    }
    
    /**
     * Creates a new SequentialFileHandler.
     * Equivalent to COBOL: SELECT ... ASSIGN TO filename
     */
    public SequentialFileHandler(String filename) {
        this.records = new ArrayList<>();
        this.filePath = Paths.get(filename);
        this.currentId = 1;
        this.readPosition = 0;
        this.isOpen = false;
        this.openMode = null;
    }
    
    /**
     * Opens the file for reading (input).
     * Equivalent to COBOL: OPEN INPUT SEQUENTIAL-FILE
     * 
     * Loads existing records from the file into memory.
     */
    public void openForRead() throws FileOperationException {
        if (isOpen) {
            throw new FileOperationException("File is already open");
        }
        
        try {
            if (Files.exists(filePath)) {
                loadFromFile();
            }
            readPosition = 0;
            isOpen = true;
            openMode = OpenMode.INPUT;
        } catch (IOException e) {
            throw new FileOperationException("Error opening file for read: " + filePath, "35", e);
        }
    }
    
    /**
     * Opens the file for writing (output).
     * Equivalent to COBOL: OPEN OUTPUT SEQUENTIAL-FILE
     * 
     * Creates a new file, discarding any existing content.
     * This is the mode used in SEQUENTIAL-EXAMPLE.cbl.
     */
    public void openForWrite() throws FileOperationException {
        if (isOpen) {
            throw new FileOperationException("File is already open");
        }
        
        records.clear();
        currentId = 1;
        isOpen = true;
        openMode = OpenMode.OUTPUT;
    }
    
    /**
     * Opens the file for appending (extend).
     * Equivalent to COBOL: OPEN EXTEND SEQUENTIAL-FILE
     * 
     * Loads existing records and positions for appending new records.
     */
    public void openForAppend() throws FileOperationException {
        if (isOpen) {
            throw new FileOperationException("File is already open");
        }
        
        try {
            if (Files.exists(filePath)) {
                loadFromFile();
                currentId = records.stream()
                        .mapToInt(SequentialRecord::getId)
                        .max()
                        .orElse(0) + 1;
            } else {
                currentId = 1;
            }
            isOpen = true;
            openMode = OpenMode.EXTEND;
        } catch (IOException e) {
            throw new FileOperationException("Error opening file for append: " + filePath, "35", e);
        }
    }
    
    /**
     * Closes the file and persists data to disk.
     * Equivalent to COBOL: CLOSE SEQUENTIAL-FILE
     */
    public void close() throws FileOperationException {
        if (!isOpen) {
            return;
        }
        
        try {
            if (openMode == OpenMode.OUTPUT || openMode == OpenMode.EXTEND) {
                saveToFile();
            }
            isOpen = false;
            openMode = null;
        } catch (IOException e) {
            throw new FileOperationException("Error closing file: " + filePath, e);
        }
    }
    
    /**
     * Writes a new record with auto-incrementing ID.
     * Equivalent to COBOL:
     *   MOVE CURRENT-ID TO IDNUM
     *   MOVE CURRENT-NAME TO NAME
     *   WRITE RECORD
     *   ADD 1 TO CURRENT-ID
     * 
     * @param name The name to store in the record
     * @return The record that was written (with assigned ID)
     */
    public SequentialRecord write(String name) throws FileOperationException {
        checkOpenForWrite();
        
        SequentialRecord record = new SequentialRecord(currentId, name);
        records.add(record);
        currentId++;
        
        return record;
    }
    
    /**
     * Writes a record with a specific ID.
     * Note: In COBOL sequential files, IDs are typically auto-assigned.
     * This method is provided for flexibility.
     */
    public void writeRecord(SequentialRecord record) throws FileOperationException {
        checkOpenForWrite();
        records.add(record);
    }
    
    /**
     * Reads the next record in sequence.
     * Equivalent to COBOL: READ SEQUENTIAL-FILE
     * 
     * In COBOL, reaching the end of file sets file status to "10" (FS-END-OF-FILE).
     * In Java, we return Optional.empty() to indicate end of file.
     * 
     * @return Optional containing the next record, or empty if at end of file
     */
    public Optional<SequentialRecord> readNext() throws FileOperationException {
        checkOpenForRead();
        
        if (readPosition >= records.size()) {
            return Optional.empty();
        }
        
        return Optional.of(records.get(readPosition++));
    }
    
    /**
     * Checks if there are more records to read.
     * Equivalent to checking COBOL file status for end-of-file ("10").
     */
    public boolean hasNext() throws FileOperationException {
        checkOpenForRead();
        return readPosition < records.size();
    }
    
    /**
     * Resets the read position to the beginning.
     * Equivalent to COBOL: CLOSE and OPEN INPUT (to re-read from start)
     */
    public void resetReadPosition() throws FileOperationException {
        checkOpenForRead();
        readPosition = 0;
    }
    
    /**
     * Returns all records as a stream for functional-style processing.
     * This is a Java enhancement over COBOL's sequential reading.
     * 
     * Example usage (equivalent to COBOL PERFORM UNTIL END-OF-FILE):
     *   handler.stream().forEach(record -> process(record));
     */
    public Stream<SequentialRecord> stream() throws FileOperationException {
        checkOpenForRead();
        return records.stream();
    }
    
    /**
     * Processes all records with a consumer function.
     * Equivalent to COBOL:
     *   PERFORM UNTIL NO-MORE-DATA
     *       READ SEQUENTIAL-FILE
     *       ... process record ...
     *   END-PERFORM.
     */
    public void processAll(Consumer<SequentialRecord> processor) throws FileOperationException {
        checkOpenForRead();
        records.forEach(processor);
    }
    
    /**
     * Filters records based on a predicate.
     * This is a Java enhancement - in COBOL, you would read all records
     * and check conditions in the PROCEDURE DIVISION.
     */
    public List<SequentialRecord> filter(Predicate<SequentialRecord> predicate) throws FileOperationException {
        checkOpenForRead();
        return records.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns all records as a list.
     */
    public List<SequentialRecord> getAll() throws FileOperationException {
        checkOpenForRead();
        return new ArrayList<>(records);
    }
    
    /**
     * Returns the number of records in the file.
     */
    public int size() throws FileOperationException {
        checkOpen();
        return records.size();
    }
    
    /**
     * Returns the next ID that will be assigned.
     */
    public int getNextId() {
        return currentId;
    }
    
    private void checkOpen() throws FileOperationException {
        if (!isOpen) {
            throw new FileOperationException("File is not open", "47");
        }
    }
    
    private void checkOpenForRead() throws FileOperationException {
        checkOpen();
        if (openMode != OpenMode.INPUT) {
            throw new FileOperationException("File is not open for reading", "47");
        }
    }
    
    private void checkOpenForWrite() throws FileOperationException {
        checkOpen();
        if (openMode != OpenMode.OUTPUT && openMode != OpenMode.EXTEND) {
            throw new FileOperationException("File is not open for writing", "48");
        }
    }
    
    /**
     * Loads data from the file into the ArrayList.
     */
    private void loadFromFile() throws IOException {
        records.clear();
        
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            if (line.length() >= 35) {
                try {
                    SequentialRecord record = SequentialRecord.fromCobolRecord(line);
                    records.add(record);
                } catch (Exception e) {
                    // Skip invalid records
                }
            }
        }
    }
    
    /**
     * Saves the ArrayList data to the file in COBOL record format.
     */
    private void saveToFile() throws IOException {
        List<String> lines = records.stream()
                .map(SequentialRecord::toCobolRecord)
                .collect(Collectors.toList());
        
        Files.write(filePath, lines);
    }
}
