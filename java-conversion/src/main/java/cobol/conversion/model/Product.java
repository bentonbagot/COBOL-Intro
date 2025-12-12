package cobol.conversion.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Java representation of a COBOL Product record.
 * 
 * COBOL to Java Mapping:
 * This class represents the COBOL record structure from createdat.cbl:
 * 
 *   01 PRODUCTS-RECORD.
 *       05 PROD-CODE         PIC X(5).      -> String code (5 chars)
 *       05 PROD-NAME         PIC X(20).     -> String name (20 chars)
 *       05 PROD-PRICE        PIC 9(7)V99.   -> BigDecimal price (7 digits + 2 decimal)
 *       05 PROD-STOCK        PIC 9(5).      -> int stock (5 digits)
 * 
 * COBOL PIC Clause Explanation:
 *   - PIC X(n): Alphanumeric field of n characters -> Java String
 *   - PIC 9(n): Numeric field of n digits -> Java int or long
 *   - PIC 9(n)V99: Numeric with implied decimal (V) -> Java BigDecimal
 * 
 * In COBOL, the record is stored as a fixed-length string where each field
 * occupies a specific position. In Java, we use a class with typed fields.
 */
public class Product {
    
    private static final int CODE_LENGTH = 5;
    private static final int NAME_LENGTH = 20;
    private static final int MAX_STOCK = 99999;
    
    private String code;
    private String name;
    private BigDecimal price;
    private int stock;
    
    public Product() {
    }
    
    public Product(String code, String name, BigDecimal price, int stock) {
        setCode(code);
        setName(name);
        setPrice(price);
        setStock(stock);
    }
    
    public String getCode() {
        return code;
    }
    
    /**
     * Sets the product code, padded or truncated to 5 characters.
     * In COBOL, PIC X(5) always stores exactly 5 characters, padding with spaces if needed.
     */
    public void setCode(String code) {
        this.code = padOrTruncate(code, CODE_LENGTH);
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Sets the product name, padded or truncated to 20 characters.
     * In COBOL, PIC X(20) always stores exactly 20 characters.
     */
    public void setName(String name) {
        this.name = padOrTruncate(name, NAME_LENGTH);
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    /**
     * Sets the product price.
     * In COBOL, PIC 9(7)V99 represents a number with up to 7 integer digits
     * and 2 decimal places. The 'V' is an implied decimal point.
     */
    public void setPrice(BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
    
    public int getStock() {
        return stock;
    }
    
    /**
     * Sets the stock quantity.
     * In COBOL, PIC 9(5) can store values from 0 to 99999.
     */
    public void setStock(int stock) {
        if (stock < 0 || stock > MAX_STOCK) {
            throw new IllegalArgumentException("Stock must be between 0 and " + MAX_STOCK);
        }
        this.stock = stock;
    }
    
    /**
     * Pads or truncates a string to the specified length.
     * This mimics COBOL's fixed-length field behavior.
     */
    private String padOrTruncate(String value, int length) {
        if (value == null) {
            return String.format("%-" + length + "s", "");
        }
        if (value.length() > length) {
            return value.substring(0, length);
        }
        return String.format("%-" + length + "s", value);
    }
    
    /**
     * Converts the Product to a fixed-length string representation,
     * similar to how COBOL stores records in a file.
     */
    public String toCobolRecord() {
        StringBuilder sb = new StringBuilder();
        sb.append(padOrTruncate(code, CODE_LENGTH));
        sb.append(padOrTruncate(name, NAME_LENGTH));
        String priceStr = String.format("%09d", price.movePointRight(2).intValue());
        sb.append(priceStr);
        sb.append(String.format("%05d", stock));
        return sb.toString();
    }
    
    /**
     * Creates a Product from a fixed-length COBOL record string.
     */
    public static Product fromCobolRecord(String record) {
        if (record == null || record.length() < 39) {
            throw new IllegalArgumentException("Invalid COBOL record format");
        }
        Product product = new Product();
        product.setCode(record.substring(0, 5).trim());
        product.setName(record.substring(5, 25).trim());
        String priceStr = record.substring(25, 34);
        product.setPrice(new BigDecimal(priceStr).movePointLeft(2));
        product.setStock(Integer.parseInt(record.substring(34, 39).trim()));
        return product;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(code, product.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return String.format("Product{code='%s', name='%s', price=%s, stock=%d}",
                code != null ? code.trim() : null,
                name != null ? name.trim() : null,
                price,
                stock);
    }
}
