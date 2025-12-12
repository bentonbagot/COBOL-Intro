package cobol.conversion.handler;

import cobol.conversion.exceptions.DuplicateKeyException;
import cobol.conversion.exceptions.FileOperationException;
import cobol.conversion.exceptions.RecordNotFoundException;
import cobol.conversion.model.Product;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Java implementation of COBOL Indexed File operations using HashMap.
 * 
 * COBOL to Java Mapping:
 * This class replaces COBOL indexed file operations from createdat.cbl:
 * 
 *   SELECT PRODUCTS-FILE ASSIGN TO "PRODUCTS.DAT"
 *       ORGANIZATION IS INDEXED
 *       ACCESS MODE IS DYNAMIC
 *       RECORD KEY IS PROD-CODE
 *       FILE STATUS IS FS-PRODUCTS.
 * 
 * COBOL Indexed File Characteristics:
 * - Records are accessed by a unique key (RECORD KEY)
 * - Supports random access (read/write by key) and sequential access
 * - ACCESS MODE IS DYNAMIC allows both random and sequential access
 * - Duplicate keys are not allowed (causes file status "22")
 * 
 * Java Implementation:
 * - HashMap<String, Product> provides O(1) key-based access
 * - The product code serves as the key (equivalent to RECORD KEY)
 * - File persistence uses Java NIO for reading/writing to disk
 * 
 * COBOL File Operations Mapping:
 * - OPEN I-O    -> constructor / open()
 * - WRITE       -> insert()
 * - READ        -> get()
 * - REWRITE     -> update()
 * - DELETE      -> delete()
 * - CLOSE       -> close()
 * - START       -> Not directly needed (HashMap allows direct access)
 * - READ NEXT   -> getAll() with stream processing
 */
public class IndexedFileHandler {
    
    private final Map<String, Product> indexedData;
    private final Path filePath;
    private boolean isOpen;
    
    /**
     * Creates a new IndexedFileHandler.
     * Equivalent to COBOL: SELECT ... ASSIGN TO filename
     */
    public IndexedFileHandler(String filename) {
        this.indexedData = new HashMap<>();
        this.filePath = Paths.get(filename);
        this.isOpen = false;
    }
    
    /**
     * Opens the indexed file for I/O operations.
     * Equivalent to COBOL: OPEN I-O PRODUCTS-FILE
     * 
     * If the file doesn't exist, it creates a new empty file.
     * This mimics the COBOL behavior where OPEN OUTPUT creates a new file.
     */
    public void open() throws FileOperationException {
        if (isOpen) {
            return;
        }
        
        try {
            if (Files.exists(filePath)) {
                loadFromFile();
            }
            isOpen = true;
        } catch (IOException e) {
            throw new FileOperationException("Error opening file: " + filePath, "35", e);
        }
    }
    
    /**
     * Closes the indexed file and persists data to disk.
     * Equivalent to COBOL: CLOSE PRODUCTS-FILE
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
     * Inserts a new product record.
     * Equivalent to COBOL: WRITE PRODUCTS-RECORD
     * 
     * COBOL behavior:
     *   WRITE PRODUCTS-RECORD
     *       INVALID KEY
     *           DISPLAY "ERROR: DUPLICATE CODE - " PROD-CODE
     *       NOT INVALID KEY
     *           DISPLAY "PRODUCT SUCCESSFULLY REGISTERED"
     *   END-WRITE.
     * 
     * @throws DuplicateKeyException if a product with the same code already exists (FS-DUPLICATE "22")
     */
    public void insert(Product product) throws DuplicateKeyException, FileOperationException {
        checkOpen();
        
        String key = product.getCode().trim();
        if (indexedData.containsKey(key)) {
            throw new DuplicateKeyException(key);
        }
        
        indexedData.put(key, product);
    }
    
    /**
     * Retrieves a product by its code (key).
     * Equivalent to COBOL: READ PRODUCTS-FILE
     * 
     * COBOL behavior:
     *   MOVE search-key TO PROD-CODE
     *   READ PRODUCTS-FILE
     *       INVALID KEY DISPLAY "Not found"
     *       NOT INVALID KEY DISPLAY product-details
     *   END-READ.
     * 
     * @throws RecordNotFoundException if no product exists with the given code (FS-NOT-FOUND "23")
     */
    public Product get(String code) throws RecordNotFoundException, FileOperationException {
        checkOpen();
        
        String key = code.trim();
        Product product = indexedData.get(key);
        if (product == null) {
            throw new RecordNotFoundException(key);
        }
        
        return product;
    }
    
    /**
     * Updates an existing product record.
     * Equivalent to COBOL: REWRITE PRODUCTS-RECORD
     * 
     * In COBOL, REWRITE replaces the record that was last read.
     * The record key cannot be changed during a REWRITE operation.
     * 
     * @throws RecordNotFoundException if no product exists with the given code (FS-NOT-FOUND "23")
     */
    public void update(Product product) throws RecordNotFoundException, FileOperationException {
        checkOpen();
        
        String key = product.getCode().trim();
        if (!indexedData.containsKey(key)) {
            throw new RecordNotFoundException(key);
        }
        
        indexedData.put(key, product);
    }
    
    /**
     * Deletes a product record by its code.
     * Equivalent to COBOL: DELETE PRODUCTS-FILE
     * 
     * In COBOL, DELETE removes the record that was last read.
     * 
     * @throws RecordNotFoundException if no product exists with the given code (FS-NOT-FOUND "23")
     */
    public void delete(String code) throws RecordNotFoundException, FileOperationException {
        checkOpen();
        
        String key = code.trim();
        if (!indexedData.containsKey(key)) {
            throw new RecordNotFoundException(key);
        }
        
        indexedData.remove(key);
    }
    
    /**
     * Checks if a product exists with the given code.
     * This is a convenience method not directly available in COBOL.
     * In COBOL, you would attempt a READ and check the file status.
     */
    public boolean exists(String code) throws FileOperationException {
        checkOpen();
        return indexedData.containsKey(code.trim());
    }
    
    /**
     * Returns all products in the indexed file.
     * Equivalent to COBOL sequential reading with READ NEXT:
     * 
     *   START PRODUCTS-FILE KEY IS >= low-value
     *   PERFORM UNTIL FS-END-OF-FILE
     *       READ PRODUCTS-FILE NEXT
     *       ... process record ...
     *   END-PERFORM.
     */
    public List<Product> getAll() throws FileOperationException {
        checkOpen();
        return new ArrayList<>(indexedData.values());
    }
    
    /**
     * Returns all products sorted by their code (key).
     * In COBOL indexed files, sequential reading returns records
     * in key order.
     */
    public List<Product> getAllSorted() throws FileOperationException {
        checkOpen();
        return indexedData.values().stream()
                .sorted(Comparator.comparing(p -> p.getCode().trim()))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns the number of records in the file.
     */
    public int size() throws FileOperationException {
        checkOpen();
        return indexedData.size();
    }
    
    private void checkOpen() throws FileOperationException {
        if (!isOpen) {
            throw new FileOperationException("File is not open", "47");
        }
    }
    
    /**
     * Loads data from the file into the HashMap.
     * This reads the COBOL-format records and converts them to Product objects.
     */
    private void loadFromFile() throws IOException {
        indexedData.clear();
        
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            if (line.length() >= 39) {
                try {
                    Product product = Product.fromCobolRecord(line);
                    indexedData.put(product.getCode().trim(), product);
                } catch (Exception e) {
                    // Skip invalid records
                }
            }
        }
    }
    
    /**
     * Saves the HashMap data to the file in COBOL record format.
     */
    private void saveToFile() throws IOException {
        List<String> lines = indexedData.values().stream()
                .sorted(Comparator.comparing(p -> p.getCode().trim()))
                .map(Product::toCobolRecord)
                .collect(Collectors.toList());
        
        Files.write(filePath, lines);
    }
}
