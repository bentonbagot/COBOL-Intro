# 🖥️ COBOL Projects — File Management & Business Logic

This repository contains a collection of COBOL programs that simulate basic business processes such as inventory, sales, and customer management. These programs showcase how to work with **sequential**, **indexed**, and **relative** files in COBOL.

> 💡 Suitable for students, COBOL learners, or those interested in classic enterprise file processing systems.

---

### 🌐 Language Versions

This repository includes two versions of the COBOL programs:

- 📁 `Spanish-COBOL/` — Programs written with **Spanish identifiers and comments**.
- 📁 `English-COBOL/` — Translated versions with **English identifiers and comments**.

Each folder mirrors the same functionality, so learners can choose based on their preferred language.

> ✅ Ideal for bilingual education or transitioning from Spanish to English in COBOL projects.

---

| COBOL Program                | Description                                                                 | File Type(s)         |
|-----------------------------|-----------------------------------------------------------------------------|----------------------|
| `CREATE-PRODUCTS.cob`       | Creates `PRODUCTS.DAT` with predefined product stock data.                 | Indexed              |
| `NEW-PRODUCTS.cob`     | Adds new products with code, name, price, and stock.                        | Indexed              |
| `CREATE-SELLS.cob`          | Generates `SELLS.DAT` with predefined sales.                               | Indexed + Sequential  |
| `UPDATE-PRODUCTS.cob`  | Updates stock in `PRODUCTS.DAT` based on `SELLS.DAT`.                     | Indexed + Sequential  |
| `EXAMPLE-INDEX.cob`      | Adds/searches customers in `clients.idx` using indexed access.             | Indexed              |
| `SEQUENCIAL-EXAMPLE.cob`    | Saves user-input names in a sequential file with auto-increment IDs.        | Sequential           |
| `RELATIVE.cob`      | Saves records to specific positions in a relative file.                     | Relative             |
| `READ-WRITE.cob`           | Reads from `entrada.txt` and writes to `salida.txt`, line by line.          | Line Sequential      |

---

## 🧰 Requirements

You can run these programs using one of the following options:

### ✅ Option 1: GnuCOBOL (Linux/MacOS)

```bash
sudo apt install open-cobol         # Linux
brew install gnu-cobol              # MacOS (via Homebrew)
```

### Option 2: Online Compilers

Use an online COBOL compiler such as [JDoodle](https://www.jdoodle.com/execute-cobol-online/) or [Tutorialspoint](https://www.tutorialspoint.com/compile_cobol_online.php).

---

## Java Migration

This repository also contains a complete Java migration of the COBOL programs, using Maven, H2 embedded database, and JDBC.

### Build

```bash
cd java
mvn clean install
```

### Run

```bash
# Interactive product entry
mvn exec:java -Dexec.mainClass="com.cobolintro.app.ProductEntryApp"

# Sales registration
mvn exec:java -Dexec.mainClass="com.cobolintro.app.SalesApp"

# Customer management
mvn exec:java -Dexec.mainClass="com.cobolintro.app.CustomerApp"

# File demos and arithmetic
mvn exec:java -Dexec.mainClass="com.cobolintro.app.DemoApp"
```

### COBOL-to-Java Mapping

| COBOL Program | Java Equivalent | Description |
|---|---|---|
| createdat.cbl | ProductEntryApp + ProductService + ProductDao | Product entry with indexed file |
| excercise2.cbl | ProductService.initializeProducts() | Batch product loading |
| excercise3-sells.cbl | SalesApp + SaleService + SaleDao | Sales with cross-file validation |
| EXAMPLE-INDEX.cbl | CustomerApp + CustomerService + CustomerDao | Customer management with indexed access |
| SEQUENTIAL-EXAMPLE.cbl | FileDemoService.sequentialDemo() + SequentialFileDao | Sequential file operations |
| Relative.cbl | FileDemoService.relativeDemo() + RelativeFileDao | Relative file access |
| read-write.cbl | FileDemoService.readWriteDemo() + FileCopyDao | Line-by-line file copy |
| aritmeti.cbl | FileDemoService.arithmeticDemo() | Arithmetic operations |

For detailed migration decisions, see [MIGRATION.md](MIGRATION.md).
