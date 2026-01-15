package com.cobolmodernization.validation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing COBOL fixed-length record files.
 * Supports parsing of PRODUCTS.DAT, SELLS.DAT, and clients.idx files
 * to compare with Java JPA entities.
 * 
 * COBOL Record Structures:
 * - Product: PROD-CODE(5) + PROD-NAME(20) + PROD-PRICE(9) + PROD-STOCK(5) = 39 bytes
 * - Sale: VENTA-CODIGO(5) + VENTA-CANTIDAD(5) = 10 bytes
 * - Customer: CLAVE-CLIENTE(10) + NOMBRE-CLIENTE(30) + TELEFONO(15) = 55 bytes
 * - Sequential: IDNUM(5) + NAME(30) = 35 bytes
 */
public class CobolDataParser {

    public static class CobolProduct {
        public String code;
        public String name;
        public BigDecimal price;
        public int stock;

        public CobolProduct(String code, String name, BigDecimal price, int stock) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return String.format("Product[code=%s, name=%s, price=%s, stock=%d]",
                    code, name, price, stock);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CobolProduct)) return false;
            CobolProduct other = (CobolProduct) obj;
            return code.equals(other.code) &&
                   name.trim().equals(other.name.trim()) &&
                   price.compareTo(other.price) == 0 &&
                   stock == other.stock;
        }
    }

    public static class CobolSale {
        public String productCode;
        public int quantity;

        public CobolSale(String productCode, int quantity) {
            this.productCode = productCode;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return String.format("Sale[productCode=%s, quantity=%d]", productCode, quantity);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CobolSale)) return false;
            CobolSale other = (CobolSale) obj;
            return productCode.equals(other.productCode) && quantity == other.quantity;
        }
    }

    public static class CobolCustomer {
        public String customerId;
        public String name;
        public String phone;

        public CobolCustomer(String customerId, String name, String phone) {
            this.customerId = customerId;
            this.name = name;
            this.phone = phone;
        }

        @Override
        public String toString() {
            return String.format("Customer[id=%s, name=%s, phone=%s]", customerId, name, phone);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CobolCustomer)) return false;
            CobolCustomer other = (CobolCustomer) obj;
            return customerId.equals(other.customerId) &&
                   name.trim().equals(other.name.trim()) &&
                   phone.trim().equals(other.phone.trim());
        }
    }

    public static class CobolSequentialRecord {
        public int id;
        public String name;

        public CobolSequentialRecord(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("SequentialRecord[id=%d, name=%s]", id, name);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CobolSequentialRecord)) return false;
            CobolSequentialRecord other = (CobolSequentialRecord) obj;
            return id == other.id && name.trim().equals(other.name.trim());
        }
    }

    private static final int PRODUCT_RECORD_SIZE = 39;
    private static final int SALE_RECORD_SIZE = 10;
    private static final int CUSTOMER_RECORD_SIZE = 55;
    private static final int SEQUENTIAL_RECORD_SIZE = 35;

    /**
     * Parse COBOL PRODUCTS.DAT indexed file.
     * Note: GnuCOBOL indexed files have a header structure that varies.
     * This method attempts to parse the data records.
     */
    public static List<CobolProduct> parseProductsFile(Path filePath) throws IOException {
        List<CobolProduct> products = new ArrayList<>();
        
        if (!Files.exists(filePath)) {
            return products;
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        
        int offset = findDataStartOffset(fileContent, PRODUCT_RECORD_SIZE);
        
        while (offset + PRODUCT_RECORD_SIZE <= fileContent.length) {
            byte[] record = new byte[PRODUCT_RECORD_SIZE];
            System.arraycopy(fileContent, offset, record, 0, PRODUCT_RECORD_SIZE);
            
            if (isValidProductRecord(record)) {
                CobolProduct product = parseProductRecord(record);
                if (product != null && !product.code.trim().isEmpty()) {
                    products.add(product);
                }
            }
            
            offset += PRODUCT_RECORD_SIZE;
        }
        
        return products;
    }

    /**
     * Parse COBOL SELLS.DAT sequential file.
     */
    public static List<CobolSale> parseSalesFile(Path filePath) throws IOException {
        List<CobolSale> sales = new ArrayList<>();
        
        if (!Files.exists(filePath)) {
            return sales;
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        
        int offset = 0;
        while (offset + SALE_RECORD_SIZE <= fileContent.length) {
            byte[] record = new byte[SALE_RECORD_SIZE];
            System.arraycopy(fileContent, offset, record, 0, SALE_RECORD_SIZE);
            
            CobolSale sale = parseSaleRecord(record);
            if (sale != null && !sale.productCode.trim().isEmpty()) {
                sales.add(sale);
            }
            
            offset += SALE_RECORD_SIZE;
        }
        
        return sales;
    }

    /**
     * Parse COBOL clients.idx indexed file.
     */
    public static List<CobolCustomer> parseCustomersFile(Path filePath) throws IOException {
        List<CobolCustomer> customers = new ArrayList<>();
        
        if (!Files.exists(filePath)) {
            return customers;
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        
        int offset = findDataStartOffset(fileContent, CUSTOMER_RECORD_SIZE);
        
        while (offset + CUSTOMER_RECORD_SIZE <= fileContent.length) {
            byte[] record = new byte[CUSTOMER_RECORD_SIZE];
            System.arraycopy(fileContent, offset, record, 0, CUSTOMER_RECORD_SIZE);
            
            if (isValidCustomerRecord(record)) {
                CobolCustomer customer = parseCustomerRecord(record);
                if (customer != null && !customer.customerId.trim().isEmpty()) {
                    customers.add(customer);
                }
            }
            
            offset += CUSTOMER_RECORD_SIZE;
        }
        
        return customers;
    }

    /**
     * Parse COBOL sequential data file (datos.dat).
     */
    public static List<CobolSequentialRecord> parseSequentialFile(Path filePath) throws IOException {
        List<CobolSequentialRecord> records = new ArrayList<>();
        
        if (!Files.exists(filePath)) {
            return records;
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        
        int offset = 0;
        while (offset + SEQUENTIAL_RECORD_SIZE <= fileContent.length) {
            byte[] record = new byte[SEQUENTIAL_RECORD_SIZE];
            System.arraycopy(fileContent, offset, record, 0, SEQUENTIAL_RECORD_SIZE);
            
            CobolSequentialRecord seqRecord = parseSequentialRecord(record);
            if (seqRecord != null) {
                records.add(seqRecord);
            }
            
            offset += SEQUENTIAL_RECORD_SIZE;
        }
        
        return records;
    }

    /**
     * Parse line sequential text file (entrada.txt style).
     */
    public static List<String> parseLineSequentialFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }

    private static int findDataStartOffset(byte[] fileContent, int recordSize) {
        for (int i = 0; i < Math.min(512, fileContent.length - recordSize); i++) {
            if (isPrintableAscii(fileContent[i])) {
                return i;
            }
        }
        return 0;
    }

    private static boolean isPrintableAscii(byte b) {
        return b >= 0x20 && b <= 0x7E;
    }

    private static boolean isValidProductRecord(byte[] record) {
        for (int i = 0; i < 5; i++) {
            if (!isPrintableAscii(record[i]) && record[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidCustomerRecord(byte[] record) {
        for (int i = 0; i < 10; i++) {
            if (!isPrintableAscii(record[i]) && record[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private static CobolProduct parseProductRecord(byte[] record) {
        try {
            String code = new String(record, 0, 5, StandardCharsets.US_ASCII).trim();
            String name = new String(record, 5, 20, StandardCharsets.US_ASCII).trim();
            String priceStr = new String(record, 25, 9, StandardCharsets.US_ASCII).trim();
            String stockStr = new String(record, 34, 5, StandardCharsets.US_ASCII).trim();
            
            if (code.isEmpty()) {
                return null;
            }
            
            BigDecimal price = BigDecimal.ZERO;
            if (!priceStr.isEmpty() && priceStr.matches("\\d+")) {
                long priceValue = Long.parseLong(priceStr);
                price = BigDecimal.valueOf(priceValue, 2);
            }
            
            int stock = 0;
            if (!stockStr.isEmpty() && stockStr.matches("\\d+")) {
                stock = Integer.parseInt(stockStr);
            }
            
            return new CobolProduct(code, name, price, stock);
        } catch (Exception e) {
            return null;
        }
    }

    private static CobolSale parseSaleRecord(byte[] record) {
        try {
            String productCode = new String(record, 0, 5, StandardCharsets.US_ASCII).trim();
            String quantityStr = new String(record, 5, 5, StandardCharsets.US_ASCII).trim();
            
            if (productCode.isEmpty()) {
                return null;
            }
            
            int quantity = 0;
            if (!quantityStr.isEmpty() && quantityStr.matches("\\d+")) {
                quantity = Integer.parseInt(quantityStr);
            }
            
            return new CobolSale(productCode, quantity);
        } catch (Exception e) {
            return null;
        }
    }

    private static CobolCustomer parseCustomerRecord(byte[] record) {
        try {
            String customerId = new String(record, 0, 10, StandardCharsets.US_ASCII).trim();
            String name = new String(record, 10, 30, StandardCharsets.US_ASCII).trim();
            String phone = new String(record, 40, 15, StandardCharsets.US_ASCII).trim();
            
            if (customerId.isEmpty()) {
                return null;
            }
            
            return new CobolCustomer(customerId, name, phone);
        } catch (Exception e) {
            return null;
        }
    }

    private static CobolSequentialRecord parseSequentialRecord(byte[] record) {
        try {
            String idStr = new String(record, 0, 5, StandardCharsets.US_ASCII).trim();
            String name = new String(record, 5, 30, StandardCharsets.US_ASCII).trim();
            
            if (idStr.isEmpty() || !idStr.matches("\\d+")) {
                return null;
            }
            
            int id = Integer.parseInt(idStr);
            return new CobolSequentialRecord(id, name);
        } catch (Exception e) {
            return null;
        }
    }
}
