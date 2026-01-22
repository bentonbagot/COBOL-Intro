# COBOL Programs Inventory

This document provides a comprehensive inventory of all COBOL programs in the repository, documenting their purposes, file organizations, and data files used.

## Overview

The repository contains educational COBOL programs demonstrating file management and business logic. Programs are available in both English (`English-COBOL/`) and Spanish (`Spanish-COBOL/`) versions with identical functionality.

## Program Categories

### Product Inventory Management

#### CREATE-PRODUCTS (excercise2.cbl)

Creates the initial product inventory file with predefined stock data.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/excercise2.cbl` / `Spanish-COBOL/excercise2.cbl` |
| Program ID | CREAR-PRODUCTOS |
| File Organization | Indexed |
| Data Files | Writes: `PRODUCTOS.DAT` |
| Primary Function | Initializes product inventory with 3 predefined products (codes 00001-00003) |

The program uses a simplified product record structure containing only product code (PIC X(5)) and stock quantity (PIC 9(5)).

#### NEW-PRODUCTS (Excercise1.cbl)

Interactive program for adding new products to the inventory.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/Excercise1.cbl` / `Spanish-COBOL/Excercise1.cbl` |
| Program ID | CAPTURA-PRODUCTOS |
| File Organization | Indexed (Dynamic Access) |
| Data Files | Reads/Writes: `PRODUCTOS.DAT` |
| Primary Function | Captures new product entries with full details |

Product record structure:
- `PROD-CODIGO` (PIC X(5)) - Product code (record key)
- `PROD-NOMBRE` (PIC X(20)) - Product name
- `PROD-PRECIO` (PIC 9(7)V99) - Price with 2 decimal places
- `PROD-STOCK` (PIC 9(5)) - Stock quantity

#### PRODUCT-ENTRY (createdat.cbl)

Alternative product entry program with enhanced error handling.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/createdat.cbl` / `Spanish-COBOL/createdat.cbl` |
| Program ID | PRODUCT-ENTRY |
| File Organization | Indexed (Dynamic Access) |
| Data Files | Reads/Writes: `PRODUCTS.DAT` |
| Primary Function | Product entry with automatic file creation and recovery |

This program includes robust file status handling for scenarios like file not found (status 35) and invalid organization (status 30), automatically creating or recreating the file as needed.

### Sales Processing

#### CREATE-SELLS (excercise3-sells.cbl)

Generates sales transaction records based on existing product inventory.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/excercise3-sells.cbl` / `Spanish-COBOL/excercise3-sells.cbl` |
| Program ID | CREATE-SELLS (English) / CREAR-VENTAS (Spanish) |
| File Organization | Indexed (Products) + Sequential (Sales) |
| Data Files | Reads: `PRODUCTS.DAT` / `PRODUCTOS.DAT`, Writes: `SELLS.DAT` / `VENTAS.DAT` |
| Primary Function | Creates sales records after validating products exist |

Sales record structure:
- `VENTA-CODIGO` (PIC X(5)) - Product code
- `VENTA-CANTIDAD` (PIC 9(5)) - Quantity sold

The program validates each sale against the product file before writing to ensure referential integrity.

### Customer Management

#### EXAMPLE-INDEX (EXAMPLE-INDEX.cbl / EJEMPLO-INDEXADO.cbl)

Interactive customer management system using indexed file access.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/EXAMPLE-INDEX.cbl` / `Spanish-COBOL/EJEMPLO-INDEXADO.cbl` |
| Program ID | EJEMPLO-INDEXADO |
| File Organization | Indexed (Dynamic Access) |
| Data Files | Reads/Writes: `clientes.idx` / `clientes.idx` |
| Primary Function | Add and search customer records by key |

Customer record structure:
- `CLAVE-CLIENTE` (PIC X(10)) - Customer key (record key)
- `NOMBRE-CLIENTE` (PIC X(30)) - Customer name
- `TELEFONO` (PIC X(15)) - Phone number

Provides a menu-driven interface for adding new customers (A), searching by key (B), and finishing (F).

### File Type Demonstrations

#### Sequential File Example (SEQUENTIAL-EXAMPLE.cbl / EJEMPLO-SECUENCIAL.cbl)

Demonstrates sequential file organization with auto-incrementing IDs.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/SEQUENTIAL-EXAMPLE.cbl` / `Spanish-COBOL/EJEMPLO-SECUENCIAL.cbl` |
| Program ID | SEQUENTIAL-EXAMPLE / EJEMPLO-SECUENCIAL |
| File Organization | Sequential |
| Data Files | Writes: `datos.dat` |
| Primary Function | Saves user-input names with auto-generated sequential IDs |

Record structure:
- `IDNUM` (PIC 9(5)) - Auto-incremented ID
- `NAME` / `NOMBRE` (PIC X(30)) - Name field

Users enter names until typing "FIN" to terminate input.

#### Relative File Example (Relative.cbl)

Demonstrates relative file organization with direct position-based access.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/Relative.cbl` / `Spanish-COBOL/Relative.cbl` |
| Program ID | RELATIVE-EXAMPLE / EJEMPLO-RELATIVO |
| File Organization | Relative (Dynamic Access) |
| Data Files | Reads/Writes: `relativo.dat` |
| Primary Function | Saves records to specific positions in a relative file |

Record structure:
- `IDNUM` (PIC 9(5)) - Record number
- `DA` / `DATO` (PIC X(20)) - Data field

Users specify the exact record position (1-99999) where data should be stored.

#### Line Sequential Example (read-write.cbl / lee-escribe.cbl)

Demonstrates line sequential file processing for text file copying.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/read-write.cbl` / `Spanish-COBOL/lee-escribe.cbl` |
| Program ID | read-write / lee-escribe |
| File Organization | Line Sequential |
| Data Files | Reads: `entrada.txt`, Writes: `salida.txt` |
| Primary Function | Reads from input text file and writes to output text file line by line |

Record structure:
- Input/Output records (PIC X(80)) - 80-character text lines

### Utility Programs

#### Arithmetic Demo (aritmeti.cbl)

Demonstrates COBOL arithmetic operations without file I/O.

| Attribute | Value |
|-----------|-------|
| File Path | `English-COBOL/aritmeti.cbl` / `Spanish-COBOL/aritmeti.cbl` |
| Program ID | Aritmeti |
| File Organization | N/A (no file operations) |
| Data Files | None |
| Primary Function | Demonstrates ADD, SUBTRACT, MULTIPLY, DIVIDE, and COMPUTE statements |

## Data Files Summary

| File Name | Type | Used By |
|-----------|------|---------|
| `PRODUCTOS.DAT` / `PRODUCTS.DAT` | Indexed | Excercise1.cbl, excercise2.cbl, excercise3-sells.cbl, createdat.cbl |
| `SELLS.DAT` / `VENTAS.DAT` | Sequential | excercise3-sells.cbl |
| `clientes.idx` | Indexed | EXAMPLE-INDEX.cbl / EJEMPLO-INDEXADO.cbl |
| `datos.dat` | Sequential | SEQUENTIAL-EXAMPLE.cbl / EJEMPLO-SECUENCIAL.cbl |
| `relativo.dat` | Relative | Relative.cbl |
| `entrada.txt` | Line Sequential | read-write.cbl / lee-escribe.cbl |
| `salida.txt` | Line Sequential | read-write.cbl / lee-escribe.cbl |

## File Organization Types Used

The repository demonstrates all four COBOL file organization types:

1. **Indexed** - Records accessed by key value, supports random and sequential access. Used for products and customers where direct lookup by code/key is needed.

2. **Sequential** - Records accessed in order they were written. Used for sales transactions and simple data entry.

3. **Relative** - Records accessed by relative record number (position). Demonstrates direct positional access.

4. **Line Sequential** - Text files with records delimited by line endings. Used for text file processing.

## Data Structures for Java Migration

### Product Entity
```
code: String (5 chars)
name: String (20 chars)
price: BigDecimal (7 digits, 2 decimal places)
stock: int (5 digits)
```

### Customer Entity
```
key: String (10 chars)
name: String (30 chars)
phone: String (15 chars)
```

### Sale Entity
```
productCode: String (5 chars)
quantity: int (5 digits)
```

### Generic Record (for Sequential/Relative demos)
```
id: int (5 digits)
data: String (20-30 chars)
```
