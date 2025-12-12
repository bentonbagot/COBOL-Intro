package cobol.conversion;

import cobol.conversion.exceptions.DuplicateKeyException;
import cobol.conversion.exceptions.FileOperationException;
import cobol.conversion.exceptions.RecordNotFoundException;
import cobol.conversion.handler.IndexedFileHandler;
import cobol.conversion.handler.RelativeFileHandler;
import cobol.conversion.handler.SequentialFileHandler;
import cobol.conversion.model.Product;
import cobol.conversion.model.RelativeRecord;
import cobol.conversion.model.SequentialRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Demonstration of COBOL to Java file operation conversions.
 * 
 * This class demonstrates how COBOL file operations are converted to Java:
 * 
 * 1. Indexed Files (createdat.cbl) -> HashMap-based IndexedFileHandler
 *    - Random access by key (product code)
 *    - Insert, update, delete, and search operations
 * 
 * 2. Sequential Files (SEQUENTIAL-EXAMPLE.cbl) -> ArrayList/Stream-based SequentialFileHandler
 *    - Records processed in order
 *    - Auto-incrementing IDs
 *    - Stream-based processing
 * 
 * 3. Relative Files (Relative.cbl) -> ArrayList with index access RelativeFileHandler
 *    - Direct access by position (relative key)
 *    - Sparse storage support
 */
public class FileOperationDemo {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("COBOL to Java File Operations Demo");
        System.out.println("=".repeat(60));
        System.out.println();
        
        try {
            demonstrateIndexedFile();
            System.out.println();
            
            demonstrateSequentialFile();
            System.out.println();
            
            demonstrateRelativeFile();
            System.out.println();
            
            System.out.println("=".repeat(60));
            System.out.println("All demonstrations completed successfully!");
            System.out.println("=".repeat(60));
            
        } catch (Exception e) {
            System.err.println("Error during demonstration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates IndexedFileHandler operations.
     * 
     * Equivalent COBOL operations from createdat.cbl:
     * - OPEN I-O PRODUCTS-FILE
     * - WRITE PRODUCTS-RECORD (with INVALID KEY handling)
     * - READ PRODUCTS-FILE (by key)
     * - REWRITE PRODUCTS-RECORD
     * - DELETE PRODUCTS-FILE
     * - CLOSE PRODUCTS-FILE
     */
    private static void demonstrateIndexedFile() throws FileOperationException {
        System.out.println("-".repeat(60));
        System.out.println("1. INDEXED FILE DEMONSTRATION (HashMap-based)");
        System.out.println("-".repeat(60));
        System.out.println();
        System.out.println("COBOL equivalent: createdat.cbl");
        System.out.println("Organization: INDEXED, Access: DYNAMIC, Key: PROD-CODE");
        System.out.println();
        
        IndexedFileHandler handler = new IndexedFileHandler("products_demo.dat");
        
        System.out.println("Opening file (OPEN I-O PRODUCTS-FILE)...");
        handler.open();
        
        System.out.println("\n--- INSERT Operations (WRITE PRODUCTS-RECORD) ---");
        
        Product product1 = new Product("P001", "Laptop Computer", new BigDecimal("999.99"), 50);
        Product product2 = new Product("P002", "Wireless Mouse", new BigDecimal("29.99"), 200);
        Product product3 = new Product("P003", "USB Keyboard", new BigDecimal("49.99"), 150);
        
        handler.insert(product1);
        System.out.println("Inserted: " + product1);
        
        handler.insert(product2);
        System.out.println("Inserted: " + product2);
        
        handler.insert(product3);
        System.out.println("Inserted: " + product3);
        
        System.out.println("\n--- Duplicate Key Handling (INVALID KEY) ---");
        try {
            handler.insert(new Product("P001", "Duplicate Product", new BigDecimal("100.00"), 10));
        } catch (DuplicateKeyException e) {
            System.out.println("Caught DuplicateKeyException (COBOL status '22'): " + e.getMessage());
        }
        
        System.out.println("\n--- READ Operation (READ PRODUCTS-FILE) ---");
        try {
            Product found = handler.get("P002");
            System.out.println("Found by key 'P002': " + found);
        } catch (RecordNotFoundException e) {
            System.out.println("Not found: " + e.getMessage());
        }
        
        System.out.println("\n--- Record Not Found Handling (INVALID KEY) ---");
        try {
            handler.get("P999");
        } catch (RecordNotFoundException e) {
            System.out.println("Caught RecordNotFoundException (COBOL status '23'): " + e.getMessage());
        }
        
        System.out.println("\n--- UPDATE Operation (REWRITE PRODUCTS-RECORD) ---");
        Product updatedProduct = new Product("P002", "Wireless Mouse Pro", new BigDecimal("39.99"), 180);
        handler.update(updatedProduct);
        System.out.println("Updated P002: " + handler.get("P002"));
        
        System.out.println("\n--- DELETE Operation (DELETE PRODUCTS-FILE) ---");
        handler.delete("P003");
        System.out.println("Deleted P003. Remaining records: " + handler.size());
        
        System.out.println("\n--- Sequential Read (READ NEXT) ---");
        List<Product> allProducts = handler.getAllSorted();
        System.out.println("All products in key order:");
        for (Product p : allProducts) {
            System.out.println("  " + p);
        }
        
        System.out.println("\nClosing file (CLOSE PRODUCTS-FILE)...");
        handler.close();
    }
    
    /**
     * Demonstrates SequentialFileHandler operations.
     * 
     * Equivalent COBOL operations from SEQUENTIAL-EXAMPLE.cbl:
     * - OPEN OUTPUT SEQUENTIAL-FILE
     * - WRITE RECORD (with auto-incrementing ID)
     * - CLOSE SEQUENTIAL-FILE
     * - OPEN INPUT SEQUENTIAL-FILE
     * - READ SEQUENTIAL-FILE (until end of file)
     */
    private static void demonstrateSequentialFile() throws FileOperationException {
        System.out.println("-".repeat(60));
        System.out.println("2. SEQUENTIAL FILE DEMONSTRATION (ArrayList/Stream-based)");
        System.out.println("-".repeat(60));
        System.out.println();
        System.out.println("COBOL equivalent: SEQUENTIAL-EXAMPLE.cbl");
        System.out.println("Organization: SEQUENTIAL");
        System.out.println();
        
        SequentialFileHandler handler = new SequentialFileHandler("sequential_demo.dat");
        
        System.out.println("Opening file for write (OPEN OUTPUT SEQUENTIAL-FILE)...");
        handler.openForWrite();
        
        System.out.println("\n--- WRITE Operations (with auto-incrementing ID) ---");
        System.out.println("COBOL equivalent:");
        System.out.println("  MOVE CURRENT-ID TO IDNUM");
        System.out.println("  MOVE CURRENT-NAME TO NAME");
        System.out.println("  WRITE RECORD");
        System.out.println("  ADD 1 TO CURRENT-ID");
        System.out.println();
        
        String[] names = {"Alice Johnson", "Bob Smith", "Carol Williams", "David Brown", "Eve Davis"};
        for (String name : names) {
            SequentialRecord record = handler.write(name);
            System.out.println("Written: " + record);
        }
        
        System.out.println("\nClosing file (CLOSE SEQUENTIAL-FILE)...");
        handler.close();
        
        System.out.println("\n--- READ Operations ---");
        System.out.println("Opening file for read (OPEN INPUT SEQUENTIAL-FILE)...");
        handler.openForRead();
        
        System.out.println("\nReading sequentially (PERFORM UNTIL END-OF-FILE):");
        Optional<SequentialRecord> record;
        while ((record = handler.readNext()).isPresent()) {
            System.out.println("  Read: " + record.get());
        }
        System.out.println("End of file reached (COBOL status '10')");
        
        System.out.println("\n--- Stream Processing (Java enhancement) ---");
        handler.resetReadPosition();
        System.out.println("Using Java Streams to filter names starting with 'A' or 'B':");
        handler.stream()
                .filter(r -> r.getName().trim().startsWith("A") || r.getName().trim().startsWith("B"))
                .forEach(r -> System.out.println("  Filtered: " + r));
        
        System.out.println("\nClosing file (CLOSE SEQUENTIAL-FILE)...");
        handler.close();
    }
    
    /**
     * Demonstrates RelativeFileHandler operations.
     * 
     * Equivalent COBOL operations from Relative.cbl:
     * - OPEN I-O RELATIVE-FILE
     * - WRITE RELATIVE-RECORD (at specific position)
     * - READ RELATIVE-FILE (by relative key)
     * - REWRITE RELATIVE-RECORD
     * - DELETE RELATIVE-FILE
     * - CLOSE RELATIVE-FILE
     */
    private static void demonstrateRelativeFile() throws FileOperationException {
        System.out.println("-".repeat(60));
        System.out.println("3. RELATIVE FILE DEMONSTRATION (ArrayList with index access)");
        System.out.println("-".repeat(60));
        System.out.println();
        System.out.println("COBOL equivalent: Relative.cbl");
        System.out.println("Organization: RELATIVE, Access: DYNAMIC, Key: RECORD-NUM");
        System.out.println();
        
        RelativeFileHandler handler = new RelativeFileHandler("relative_demo.dat");
        
        System.out.println("Opening file (OPEN I-O RELATIVE-FILE)...");
        handler.open();
        
        System.out.println("\n--- WRITE Operations (at specific positions) ---");
        System.out.println("COBOL equivalent:");
        System.out.println("  MOVE record-num TO RECORD-NUM");
        System.out.println("  MOVE data TO RELATIVE-RECORD");
        System.out.println("  WRITE RELATIVE-RECORD");
        System.out.println();
        
        handler.write(1, "First Record");
        System.out.println("Written at position 1: First Record");
        
        handler.write(5, "Fifth Record");
        System.out.println("Written at position 5: Fifth Record");
        
        handler.write(10, "Tenth Record");
        System.out.println("Written at position 10: Tenth Record");
        
        handler.write(3, "Third Record");
        System.out.println("Written at position 3: Third Record");
        
        System.out.println("\n--- READ Operation (by relative key) ---");
        try {
            RelativeRecord found = handler.read(5);
            System.out.println("Read at position 5: " + found);
        } catch (RecordNotFoundException e) {
            System.out.println("Not found: " + e.getMessage());
        }
        
        System.out.println("\n--- Record Not Found (empty position) ---");
        try {
            handler.read(7);
        } catch (RecordNotFoundException e) {
            System.out.println("Caught RecordNotFoundException at position 7 (empty slot): " + e.getMessage());
        }
        
        System.out.println("\n--- UPDATE Operation (REWRITE) ---");
        handler.update(5, "Updated Fifth Record");
        System.out.println("Updated position 5: " + handler.read(5));
        
        System.out.println("\n--- DELETE Operation ---");
        handler.delete(3);
        System.out.println("Deleted position 3. Record count: " + handler.size());
        
        System.out.println("\n--- Sequential Read (READ NEXT) ---");
        System.out.println("Reading all non-null records sequentially:");
        handler.resetReadPosition();
        Optional<RelativeRecord> record;
        while ((record = handler.readNext()).isPresent()) {
            System.out.println("  Position " + record.get().getId() + ": " + record.get());
        }
        
        System.out.println("\n--- Sparse Storage Demonstration ---");
        System.out.println("Total records: " + handler.size());
        System.out.println("Max capacity: " + handler.getMaxRecords());
        System.out.println("(Relative files support sparse storage - positions 2, 4, 6-9 are empty)");
        
        System.out.println("\nClosing file (CLOSE RELATIVE-FILE)...");
        handler.close();
    }
}
