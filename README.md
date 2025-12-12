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

---

## Test Verification

This is a test change to verify PR creation workflow.

