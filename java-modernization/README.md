# COBOL to Java Modernization Project

This Java project provides a modern implementation of the COBOL inventory and sales management programs.

## Project Structure

```
src/main/java/com/cobolmodernization/
├── domain/        Entity classes representing business objects
├── repository/    Data access layer for file operations
├── service/       Business logic and processing
└── ui/            Console-based user interface
```

## Directory Contents

### domain/

This package will contain the entity classes that represent the core business objects, directly mapped from COBOL record structures:

- **Product.java** - Represents a product with code, name, price, and stock quantity. Maps to the PRODUCTOS-RECORD structure from the COBOL programs.
- **Customer.java** - Represents a customer with key, name, and phone number. Maps to the REGISTRO-CLIENTE structure.
- **Sale.java** - Represents a sales transaction with product code and quantity. Maps to the VENTAS-RECORD structure.
- **GenericRecord.java** - A flexible record class for sequential and relative file demonstrations.

### repository/

This package will contain the data access layer, providing abstractions for different file storage mechanisms:

- **ProductRepository.java** - Interface defining CRUD operations for products.
- **CustomerRepository.java** - Interface defining CRUD operations for customers.
- **SaleRepository.java** - Interface defining operations for sales records.
- **IndexedFileRepository.java** - Implementation using indexed file-like storage (HashMap-based for key access).
- **SequentialFileRepository.java** - Implementation for sequential file operations.
- **RelativeFileRepository.java** - Implementation for position-based record access.
- **LineSequentialFileRepository.java** - Implementation for text file line-by-line processing.

### service/

This package will contain the business logic layer:

- **ProductService.java** - Business logic for product management (create, update, search).
- **CustomerService.java** - Business logic for customer management.
- **SalesService.java** - Business logic for sales processing, including stock validation and updates.
- **InventoryService.java** - Orchestrates inventory operations across products and sales.
- **FileProcessingService.java** - Handles file copy and transformation operations.

### ui/

This package will contain the console-based user interface:

- **MainApplication.java** - Entry point with main menu navigation.
- **ProductUI.java** - Console interface for product operations (mirrors Excercise1.cbl functionality).
- **CustomerUI.java** - Console interface for customer management (mirrors EXAMPLE-INDEX.cbl).
- **SalesUI.java** - Console interface for sales processing.
- **ConsoleHelper.java** - Utility class for formatted console input/output.

## Building the Project

```bash
cd java-modernization
mvn clean compile
```

## Running Tests

```bash
mvn test
```

## Creating Executable JAR

```bash
mvn package
java -jar target/cobol-to-java-1.0.0-SNAPSHOT.jar
```

## Migration Notes

The Java implementation preserves the business logic from the original COBOL programs while modernizing:

1. **File Organization** - COBOL indexed files are replaced with in-memory HashMap structures that can be persisted to JSON or binary files.
2. **Data Types** - COBOL PIC clauses are mapped to appropriate Java types (String, BigDecimal, int).
3. **User Interface** - COBOL DISPLAY/ACCEPT statements are replaced with Scanner-based console I/O.
4. **Error Handling** - COBOL FILE STATUS checks are replaced with Java exceptions and try-catch blocks.
