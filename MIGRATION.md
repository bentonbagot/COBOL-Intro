# COBOL to Java Migration Guide

## What Was Migrated
- All 8 English-COBOL programs have been translated to Java equivalents
- File operations (Sequential, Indexed, Relative) mapped to H2 database + flat files
- Business logic preserved: product management, sales tracking, customer lookup
- Arithmetic operations translated using BigDecimal for fixed-point precision

## What Was Dropped
- Spanish-COBOL duplicates (identical logic, only identifier language differs)
- GnuCOBOL compiler dependency (replaced with Maven + JDK 17)
- Binary data files (.DAT, .idx) replaced with H2 embedded database

## Key Translation Decisions

| COBOL Concept | Java Equivalent | Rationale |
|---|---|---|
| PIC 9(7)V99 (fixed-point) | BigDecimal | Exact decimal arithmetic, no floating-point errors |
| Indexed file (PRODUCTS.DAT) | H2 table with PRIMARY KEY | Same key-based access semantics |
| Sequential file (SELLS.DAT) | H2 table with AUTO_INCREMENT | Preserves append-only write pattern |
| Relative file (relativo.dat) | Text file with position mapping | Position-based access preserved |
| Line Sequential file | BufferedReader/BufferedWriter | Direct line-by-line I/O |
| FILE-STATUS codes | Custom exception hierarchy | FileStatusException → DuplicateKey/RecordNotFound |
| WORKING-STORAGE | Java instance fields | Scoped to object lifetime |
| PERFORM paragraphs | Method calls | Clean OOP decomposition |
| 88-level condition names | Exception subclasses + boolean methods | Type-safe condition checking |
| ACCEPT/DISPLAY | Scanner/System.out | Standard console I/O |

## Architecture
```
app/          → CLI entry points (main methods)
service/      → Business logic layer
dao/          → Data access layer (H2 + file I/O)
model/        → POJOs (data records)
exception/    → Custom exceptions (FILE-STATUS mapping)
```

## Testing
- JUnit 5 tests cover all service and DAO methods
- H2 in-memory database used for test isolation
- Run all tests: `cd java && mvn test`
