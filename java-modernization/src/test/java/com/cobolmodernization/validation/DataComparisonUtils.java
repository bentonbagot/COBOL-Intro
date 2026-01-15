package com.cobolmodernization.validation;

import com.cobolmodernization.dto.CustomerDto;
import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.dto.SaleDto;
import com.cobolmodernization.entity.SequentialRecord;
import com.cobolmodernization.validation.CobolDataParser.CobolCustomer;
import com.cobolmodernization.validation.CobolDataParser.CobolProduct;
import com.cobolmodernization.validation.CobolDataParser.CobolSale;
import com.cobolmodernization.validation.CobolDataParser.CobolSequentialRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for comparing COBOL file data with Java JPA entities.
 * Provides methods to validate data consistency between the original
 * COBOL programs and the modernized Java implementation.
 */
public class DataComparisonUtils {

    public static class ComparisonResult {
        private boolean match;
        private List<String> differences;
        private int cobolRecordCount;
        private int javaRecordCount;

        public ComparisonResult() {
            this.match = true;
            this.differences = new ArrayList<>();
        }

        public void addDifference(String difference) {
            this.match = false;
            this.differences.add(difference);
        }

        public boolean isMatch() {
            return match;
        }

        public List<String> getDifferences() {
            return differences;
        }

        public int getCobolRecordCount() {
            return cobolRecordCount;
        }

        public void setCobolRecordCount(int count) {
            this.cobolRecordCount = count;
        }

        public int getJavaRecordCount() {
            return javaRecordCount;
        }

        public void setJavaRecordCount(int count) {
            this.javaRecordCount = count;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ComparisonResult{match=").append(match);
            sb.append(", cobolRecords=").append(cobolRecordCount);
            sb.append(", javaRecords=").append(javaRecordCount);
            if (!differences.isEmpty()) {
                sb.append(", differences=[");
                for (int i = 0; i < Math.min(5, differences.size()); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(differences.get(i));
                }
                if (differences.size() > 5) {
                    sb.append(", ... and ").append(differences.size() - 5).append(" more");
                }
                sb.append("]");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    /**
     * Compare COBOL products with Java ProductDto list.
     */
    public static ComparisonResult compareProducts(List<CobolProduct> cobolProducts, 
                                                    List<ProductDto> javaProducts) {
        ComparisonResult result = new ComparisonResult();
        result.setCobolRecordCount(cobolProducts.size());
        result.setJavaRecordCount(javaProducts.size());

        if (cobolProducts.size() != javaProducts.size()) {
            result.addDifference(String.format("Record count mismatch: COBOL=%d, Java=%d",
                    cobolProducts.size(), javaProducts.size()));
        }

        for (CobolProduct cobolProduct : cobolProducts) {
            ProductDto javaProduct = findProductByCode(javaProducts, cobolProduct.code);
            if (javaProduct == null) {
                result.addDifference("Missing in Java: " + cobolProduct);
            } else {
                compareProductFields(cobolProduct, javaProduct, result);
            }
        }

        for (ProductDto javaProduct : javaProducts) {
            CobolProduct cobolProduct = findCobolProductByCode(cobolProducts, javaProduct.getCode());
            if (cobolProduct == null) {
                result.addDifference("Extra in Java: " + javaProduct);
            }
        }

        return result;
    }

    /**
     * Compare COBOL sales with Java SaleDto list.
     */
    public static ComparisonResult compareSales(List<CobolSale> cobolSales, 
                                                 List<SaleDto> javaSales) {
        ComparisonResult result = new ComparisonResult();
        result.setCobolRecordCount(cobolSales.size());
        result.setJavaRecordCount(javaSales.size());

        if (cobolSales.size() != javaSales.size()) {
            result.addDifference(String.format("Record count mismatch: COBOL=%d, Java=%d",
                    cobolSales.size(), javaSales.size()));
        }

        for (int i = 0; i < Math.min(cobolSales.size(), javaSales.size()); i++) {
            CobolSale cobolSale = cobolSales.get(i);
            SaleDto javaSale = javaSales.get(i);
            compareSaleFields(cobolSale, javaSale, i, result);
        }

        return result;
    }

    /**
     * Compare COBOL customers with Java CustomerDto list.
     */
    public static ComparisonResult compareCustomers(List<CobolCustomer> cobolCustomers, 
                                                     List<CustomerDto> javaCustomers) {
        ComparisonResult result = new ComparisonResult();
        result.setCobolRecordCount(cobolCustomers.size());
        result.setJavaRecordCount(javaCustomers.size());

        if (cobolCustomers.size() != javaCustomers.size()) {
            result.addDifference(String.format("Record count mismatch: COBOL=%d, Java=%d",
                    cobolCustomers.size(), javaCustomers.size()));
        }

        for (CobolCustomer cobolCustomer : cobolCustomers) {
            CustomerDto javaCustomer = findCustomerById(javaCustomers, cobolCustomer.customerId);
            if (javaCustomer == null) {
                result.addDifference("Missing in Java: " + cobolCustomer);
            } else {
                compareCustomerFields(cobolCustomer, javaCustomer, result);
            }
        }

        for (CustomerDto javaCustomer : javaCustomers) {
            CobolCustomer cobolCustomer = findCobolCustomerById(cobolCustomers, javaCustomer.getCustomerId());
            if (cobolCustomer == null) {
                result.addDifference("Extra in Java: " + javaCustomer);
            }
        }

        return result;
    }

    /**
     * Compare COBOL sequential records with Java SequentialRecord list.
     */
    public static ComparisonResult compareSequentialRecords(List<CobolSequentialRecord> cobolRecords, 
                                                             List<SequentialRecord> javaRecords) {
        ComparisonResult result = new ComparisonResult();
        result.setCobolRecordCount(cobolRecords.size());
        result.setJavaRecordCount(javaRecords.size());

        if (cobolRecords.size() != javaRecords.size()) {
            result.addDifference(String.format("Record count mismatch: COBOL=%d, Java=%d",
                    cobolRecords.size(), javaRecords.size()));
        }

        for (int i = 0; i < Math.min(cobolRecords.size(), javaRecords.size()); i++) {
            CobolSequentialRecord cobolRecord = cobolRecords.get(i);
            SequentialRecord javaRecord = javaRecords.get(i);
            compareSequentialRecordFields(cobolRecord, javaRecord, i, result);
        }

        return result;
    }

    /**
     * Compare line sequential file contents.
     */
    public static ComparisonResult compareLineSequentialFiles(List<String> cobolLines, 
                                                               List<String> javaLines) {
        ComparisonResult result = new ComparisonResult();
        result.setCobolRecordCount(cobolLines.size());
        result.setJavaRecordCount(javaLines.size());

        if (cobolLines.size() != javaLines.size()) {
            result.addDifference(String.format("Line count mismatch: COBOL=%d, Java=%d",
                    cobolLines.size(), javaLines.size()));
        }

        for (int i = 0; i < Math.min(cobolLines.size(), javaLines.size()); i++) {
            String cobolLine = cobolLines.get(i).trim();
            String javaLine = javaLines.get(i).trim();
            if (!cobolLine.equals(javaLine)) {
                result.addDifference(String.format("Line %d mismatch: COBOL='%s', Java='%s'",
                        i + 1, cobolLine, javaLine));
            }
        }

        return result;
    }

    /**
     * Validate that predefined products match expected COBOL CREATE-PRODUCTS data.
     */
    public static ComparisonResult validatePredefinedProducts(List<ProductDto> javaProducts) {
        ComparisonResult result = new ComparisonResult();
        result.setJavaRecordCount(javaProducts.size());
        result.setCobolRecordCount(3);

        if (javaProducts.size() != 3) {
            result.addDifference("Expected 3 predefined products, got " + javaProducts.size());
        }

        validatePredefinedProduct(javaProducts, "00001", "Product A", new BigDecimal("100.00"), 50, result);
        validatePredefinedProduct(javaProducts, "00002", "Product B", new BigDecimal("200.00"), 30, result);
        validatePredefinedProduct(javaProducts, "00003", "Product C", new BigDecimal("150.00"), 25, result);

        return result;
    }

    /**
     * Validate that predefined sales match expected COBOL CREATE-SELLS data.
     */
    public static ComparisonResult validatePredefinedSales(List<SaleDto> javaSales) {
        ComparisonResult result = new ComparisonResult();
        result.setJavaRecordCount(javaSales.size());
        result.setCobolRecordCount(5);

        if (javaSales.size() != 5) {
            result.addDifference("Expected 5 predefined sales, got " + javaSales.size());
        }

        int[][] expectedSales = {
            {0, 5},
            {1, 10},
            {2, 3},
            {3, 2},
            {4, 1}
        };
        String[] expectedCodes = {"00001", "00002", "00003", "00001", "00002"};
        int[] expectedQuantities = {5, 10, 3, 2, 1};

        for (int i = 0; i < Math.min(javaSales.size(), 5); i++) {
            SaleDto sale = javaSales.get(i);
            if (!sale.getProductCode().equals(expectedCodes[i])) {
                result.addDifference(String.format("Sale %d: expected code '%s', got '%s'",
                        i, expectedCodes[i], sale.getProductCode()));
            }
            if (sale.getQuantity() != expectedQuantities[i]) {
                result.addDifference(String.format("Sale %d: expected quantity %d, got %d",
                        i, expectedQuantities[i], sale.getQuantity()));
            }
        }

        return result;
    }

    /**
     * Validate inventory update results after processing sales.
     */
    public static ComparisonResult validateInventoryUpdate(
            List<ProductDto> productsBefore,
            List<ProductDto> productsAfter,
            List<SaleDto> processedSales) {
        
        ComparisonResult result = new ComparisonResult();
        result.setCobolRecordCount(productsBefore.size());
        result.setJavaRecordCount(productsAfter.size());

        for (ProductDto before : productsBefore) {
            ProductDto after = findProductByCode(productsAfter, before.getCode());
            if (after == null) {
                result.addDifference("Product missing after update: " + before.getCode());
                continue;
            }

            int totalSold = processedSales.stream()
                    .filter(s -> s.getProductCode().equals(before.getCode()))
                    .mapToInt(SaleDto::getQuantity)
                    .sum();

            int expectedStock = before.getStock() - totalSold;
            if (after.getStock() != expectedStock) {
                result.addDifference(String.format(
                        "Product %s: expected stock %d (was %d, sold %d), got %d",
                        before.getCode(), expectedStock, before.getStock(), totalSold, after.getStock()));
            }
        }

        return result;
    }

    private static void compareProductFields(CobolProduct cobol, ProductDto java, ComparisonResult result) {
        if (!cobol.code.equals(java.getCode())) {
            result.addDifference(String.format("Product code mismatch: COBOL='%s', Java='%s'",
                    cobol.code, java.getCode()));
        }
        if (!cobol.name.trim().equals(java.getName().trim())) {
            result.addDifference(String.format("Product name mismatch for %s: COBOL='%s', Java='%s'",
                    cobol.code, cobol.name, java.getName()));
        }
        if (cobol.price.compareTo(java.getPrice()) != 0) {
            result.addDifference(String.format("Product price mismatch for %s: COBOL=%s, Java=%s",
                    cobol.code, cobol.price, java.getPrice()));
        }
        if (cobol.stock != java.getStock()) {
            result.addDifference(String.format("Product stock mismatch for %s: COBOL=%d, Java=%d",
                    cobol.code, cobol.stock, java.getStock()));
        }
    }

    private static void compareSaleFields(CobolSale cobol, SaleDto java, int index, ComparisonResult result) {
        if (!cobol.productCode.equals(java.getProductCode())) {
            result.addDifference(String.format("Sale %d product code mismatch: COBOL='%s', Java='%s'",
                    index, cobol.productCode, java.getProductCode()));
        }
        if (cobol.quantity != java.getQuantity()) {
            result.addDifference(String.format("Sale %d quantity mismatch: COBOL=%d, Java=%d",
                    index, cobol.quantity, java.getQuantity()));
        }
    }

    private static void compareCustomerFields(CobolCustomer cobol, CustomerDto java, ComparisonResult result) {
        if (!cobol.customerId.equals(java.getCustomerId())) {
            result.addDifference(String.format("Customer ID mismatch: COBOL='%s', Java='%s'",
                    cobol.customerId, java.getCustomerId()));
        }
        if (!cobol.name.trim().equals(java.getName().trim())) {
            result.addDifference(String.format("Customer name mismatch for %s: COBOL='%s', Java='%s'",
                    cobol.customerId, cobol.name, java.getName()));
        }
        if (!Objects.equals(cobol.phone.trim(), java.getPhone() != null ? java.getPhone().trim() : "")) {
            result.addDifference(String.format("Customer phone mismatch for %s: COBOL='%s', Java='%s'",
                    cobol.customerId, cobol.phone, java.getPhone()));
        }
    }

    private static void compareSequentialRecordFields(CobolSequentialRecord cobol, SequentialRecord java, 
                                                       int index, ComparisonResult result) {
        if (!cobol.name.trim().equals(java.getName().trim())) {
            result.addDifference(String.format("Record %d name mismatch: COBOL='%s', Java='%s'",
                    index, cobol.name, java.getName()));
        }
    }

    private static void validatePredefinedProduct(List<ProductDto> products, String code, String name,
                                                   BigDecimal price, int stock, ComparisonResult result) {
        ProductDto product = findProductByCode(products, code);
        if (product == null) {
            result.addDifference("Missing predefined product: " + code);
            return;
        }
        if (!product.getName().equals(name)) {
            result.addDifference(String.format("Product %s name mismatch: expected '%s', got '%s'",
                    code, name, product.getName()));
        }
        if (product.getPrice().compareTo(price) != 0) {
            result.addDifference(String.format("Product %s price mismatch: expected %s, got %s",
                    code, price, product.getPrice()));
        }
        if (product.getStock() != stock) {
            result.addDifference(String.format("Product %s stock mismatch: expected %d, got %d",
                    code, stock, product.getStock()));
        }
    }

    private static ProductDto findProductByCode(List<ProductDto> products, String code) {
        return products.stream()
                .filter(p -> p.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    private static CobolProduct findCobolProductByCode(List<CobolProduct> products, String code) {
        return products.stream()
                .filter(p -> p.code.equals(code))
                .findFirst()
                .orElse(null);
    }

    private static CustomerDto findCustomerById(List<CustomerDto> customers, String id) {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private static CobolCustomer findCobolCustomerById(List<CobolCustomer> customers, String id) {
        return customers.stream()
                .filter(c -> c.customerId.equals(id))
                .findFirst()
                .orElse(null);
    }
}
