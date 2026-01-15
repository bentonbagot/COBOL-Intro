package com.cobolmodernization.validation;

import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.exception.DuplicateKeyException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.service.ProductService;
import com.cobolmodernization.validation.DataComparisonUtils.ComparisonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for Product data consistency.
 * Validates that Java implementation produces identical results to COBOL programs:
 * - CREATE-PRODUCTS.cob / createdat.cbl - Product master file initialization
 * - NEW-PRODUCTS.cob - Interactive product addition with duplicate detection
 * 
 * Tests verify:
 * 1. Data structure matches COBOL PRODUCTS-RECORD (PIC clauses)
 * 2. Business logic matches COBOL WRITE/READ/REWRITE operations
 * 3. Error handling matches COBOL FILE STATUS codes
 */
@SpringBootTest
@Transactional
class ProductDataValidationTest {

    @Autowired
    private ProductService productService;

    @Nested
    @DisplayName("CREATE-PRODUCTS.cob Validation - Product Initialization")
    class CreateProductsValidation {

        @Test
        @DisplayName("Predefined products match COBOL CREATE-PRODUCTS initialization data")
        void validatePredefinedProductsMatchCobol() {
            productService.initializeProducts();
            
            List<ProductDto> products = productService.findAll();
            ComparisonResult result = DataComparisonUtils.validatePredefinedProducts(products);
            
            assertTrue(result.isMatch(), 
                    "Predefined products should match COBOL CREATE-PRODUCTS data: " + result);
            assertEquals(3, products.size(), 
                    "Should have 3 predefined products as in COBOL CREATE-PRODUCTS");
        }

        @Test
        @DisplayName("Product 00001 matches COBOL predefined data")
        void validateProduct00001() {
            productService.initializeProducts();
            
            ProductDto product = productService.findByCode("00001");
            
            assertNotNull(product);
            assertEquals("00001", product.getCode());
            assertEquals("Product A", product.getName());
            assertEquals(new BigDecimal("100.00"), product.getPrice());
            assertEquals(50, product.getStock());
        }

        @Test
        @DisplayName("Product 00002 matches COBOL predefined data")
        void validateProduct00002() {
            productService.initializeProducts();
            
            ProductDto product = productService.findByCode("00002");
            
            assertNotNull(product);
            assertEquals("00002", product.getCode());
            assertEquals("Product B", product.getName());
            assertEquals(new BigDecimal("200.00"), product.getPrice());
            assertEquals(30, product.getStock());
        }

        @Test
        @DisplayName("Product 00003 matches COBOL predefined data")
        void validateProduct00003() {
            productService.initializeProducts();
            
            ProductDto product = productService.findByCode("00003");
            
            assertNotNull(product);
            assertEquals("00003", product.getCode());
            assertEquals("Product C", product.getName());
            assertEquals(new BigDecimal("150.00"), product.getPrice());
            assertEquals(25, product.getStock());
        }

        @Test
        @DisplayName("Initialize products is idempotent - matches COBOL file open behavior")
        void validateIdempotentInitialization() {
            productService.initializeProducts();
            productService.initializeProducts();
            
            List<ProductDto> products = productService.findAll();
            assertEquals(3, products.size(), 
                    "Multiple initializations should not create duplicates");
        }
    }

    @Nested
    @DisplayName("NEW-PRODUCTS.cob Validation - Interactive Product Addition")
    class NewProductsValidation {

        @Test
        @DisplayName("Create product matches COBOL WRITE PRODUCTS-RECORD")
        void validateCreateProduct() {
            ProductDto dto = ProductDto.builder()
                    .code("TEST1")
                    .name("Test Product")
                    .price(new BigDecimal("99.99"))
                    .stock(100)
                    .build();

            ProductDto created = productService.createProduct(dto);

            assertNotNull(created);
            assertEquals("TEST1", created.getCode());
            assertEquals("Test Product", created.getName());
            assertEquals(new BigDecimal("99.99"), created.getPrice());
            assertEquals(100, created.getStock());
        }

        @Test
        @DisplayName("Duplicate detection matches COBOL INVALID KEY (FILE STATUS 22)")
        void validateDuplicateKeyDetection() {
            ProductDto dto = ProductDto.builder()
                    .code("DUP01")
                    .name("First Product")
                    .price(new BigDecimal("50.00"))
                    .stock(10)
                    .build();

            productService.createProduct(dto);

            ProductDto duplicate = ProductDto.builder()
                    .code("DUP01")
                    .name("Duplicate Product")
                    .price(new BigDecimal("75.00"))
                    .stock(20)
                    .build();

            DuplicateKeyException exception = assertThrows(
                    DuplicateKeyException.class,
                    () -> productService.createProduct(duplicate)
            );

            assertEquals("Product", exception.getEntityType());
            assertEquals("DUP01", exception.getKey());
        }

        @Test
        @DisplayName("Product lookup matches COBOL READ ... KEY IS PROD-CODE")
        void validateProductLookup() {
            ProductDto dto = ProductDto.builder()
                    .code("LOOK1")
                    .name("Lookup Test")
                    .price(new BigDecimal("123.45"))
                    .stock(50)
                    .build();

            productService.createProduct(dto);

            ProductDto found = productService.findByCode("LOOK1");

            assertNotNull(found);
            assertEquals("LOOK1", found.getCode());
            assertEquals("Lookup Test", found.getName());
        }

        @Test
        @DisplayName("Not found matches COBOL INVALID KEY (FILE STATUS 23)")
        void validateNotFoundHandling() {
            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> productService.findByCode("NOTEX")
            );

            assertEquals("Product", exception.getEntityType());
            assertEquals("NOTEX", exception.getKey());
        }
    }

    @Nested
    @DisplayName("COBOL Data Structure Validation - PIC Clause Compliance")
    class DataStructureValidation {

        @Test
        @DisplayName("Product code matches COBOL PIC X(5)")
        void validateProductCodeLength() {
            ProductDto dto = ProductDto.builder()
                    .code("12345")
                    .name("Test")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            ProductDto created = productService.createProduct(dto);
            assertEquals(5, created.getCode().length());
        }

        @Test
        @DisplayName("Product name matches COBOL PIC X(20)")
        void validateProductNameLength() {
            ProductDto dto = ProductDto.builder()
                    .code("NAME1")
                    .name("12345678901234567890")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            ProductDto created = productService.createProduct(dto);
            assertEquals(20, created.getName().length());
        }

        @Test
        @DisplayName("Product price matches COBOL PIC 9(7)V99 - max value")
        void validateProductPriceMaxValue() {
            ProductDto dto = ProductDto.builder()
                    .code("PRIC1")
                    .name("Price Test")
                    .price(new BigDecimal("9999999.99"))
                    .stock(1)
                    .build();

            ProductDto created = productService.createProduct(dto);
            assertEquals(new BigDecimal("9999999.99"), created.getPrice());
        }

        @Test
        @DisplayName("Product stock matches COBOL PIC 9(5) - max value")
        void validateProductStockMaxValue() {
            ProductDto dto = ProductDto.builder()
                    .code("STCK1")
                    .name("Stock Test")
                    .price(BigDecimal.ONE)
                    .stock(99999)
                    .build();

            ProductDto created = productService.createProduct(dto);
            assertEquals(99999, created.getStock());
        }

        @Test
        @DisplayName("Product price has 2 decimal places - matches COBOL V99")
        void validateProductPriceDecimalPlaces() {
            ProductDto dto = ProductDto.builder()
                    .code("DEC01")
                    .name("Decimal Test")
                    .price(new BigDecimal("123.45"))
                    .stock(1)
                    .build();

            ProductDto created = productService.createProduct(dto);
            assertEquals(2, created.getPrice().scale());
        }
    }

    @Nested
    @DisplayName("COBOL REWRITE/DELETE Validation")
    class UpdateDeleteValidation {

        @BeforeEach
        void setUp() {
            productService.initializeProducts();
        }

        @Test
        @DisplayName("Update product matches COBOL REWRITE PRODUCTS-RECORD")
        void validateUpdateProduct() {
            ProductDto updateDto = ProductDto.builder()
                    .name("Updated Name")
                    .price(new BigDecimal("999.99"))
                    .stock(999)
                    .build();

            ProductDto updated = productService.updateProduct("00001", updateDto);

            assertEquals("00001", updated.getCode());
            assertEquals("Updated Name", updated.getName());
            assertEquals(new BigDecimal("999.99"), updated.getPrice());
            assertEquals(999, updated.getStock());
        }

        @Test
        @DisplayName("Update non-existent product throws RecordNotFoundException")
        void validateUpdateNotFound() {
            ProductDto updateDto = ProductDto.builder()
                    .name("Test")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            assertThrows(RecordNotFoundException.class,
                    () -> productService.updateProduct("NOTEX", updateDto));
        }

        @Test
        @DisplayName("Delete product matches COBOL DELETE PRODUCTS-FILE")
        void validateDeleteProduct() {
            assertTrue(productService.existsByCode("00001"));
            
            productService.deleteProduct("00001");
            
            assertFalse(productService.existsByCode("00001"));
        }

        @Test
        @DisplayName("Delete non-existent product throws RecordNotFoundException")
        void validateDeleteNotFound() {
            assertThrows(RecordNotFoundException.class,
                    () -> productService.deleteProduct("NOTEX"));
        }
    }

    @Nested
    @DisplayName("Sequential Read Validation - COBOL READ NEXT")
    class SequentialReadValidation {

        @BeforeEach
        void setUp() {
            productService.initializeProducts();
        }

        @Test
        @DisplayName("Find all products matches COBOL sequential read")
        void validateFindAllProducts() {
            List<ProductDto> products = productService.findAll();

            assertEquals(3, products.size());
            assertTrue(products.stream().anyMatch(p -> p.getCode().equals("00001")));
            assertTrue(products.stream().anyMatch(p -> p.getCode().equals("00002")));
            assertTrue(products.stream().anyMatch(p -> p.getCode().equals("00003")));
        }

        @Test
        @DisplayName("Products contain all required fields")
        void validateProductFields() {
            List<ProductDto> products = productService.findAll();

            for (ProductDto product : products) {
                assertNotNull(product.getCode(), "Product code should not be null");
                assertNotNull(product.getName(), "Product name should not be null");
                assertNotNull(product.getPrice(), "Product price should not be null");
                assertNotNull(product.getStock(), "Product stock should not be null");
            }
        }
    }
}
