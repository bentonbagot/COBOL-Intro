package com.cobolmodernization.validation;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.dto.CustomerDto;
import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.dto.SaleDto;
import com.cobolmodernization.exception.DuplicateKeyException;
import com.cobolmodernization.exception.InsufficientStockException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.service.CustomerService;
import com.cobolmodernization.service.ProductService;
import com.cobolmodernization.service.SalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for Error Handling consistency.
 * Validates that Java exception handling matches COBOL FILE STATUS error codes.
 * 
 * COBOL FILE STATUS to Java Exception Mapping:
 * - FILE STATUS 00 (success) -> normal return
 * - FILE STATUS 22 (duplicate key) -> DuplicateKeyException
 * - FILE STATUS 23 (record not found) -> RecordNotFoundException
 * - FILE STATUS 35 (file not exist) -> handled during initialization
 * - Insufficient stock -> InsufficientStockException (enhanced from COBOL)
 * 
 * Tests verify:
 * 1. Exception types match COBOL FILE STATUS semantics
 * 2. Exception messages provide meaningful information
 * 3. Exception handling is consistent across all services
 */
@SpringBootTest
@Transactional
class ErrorHandlingValidationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private SalesService salesService;

    @Autowired
    private CustomerService customerService;

    @Nested
    @DisplayName("FILE STATUS 22 - Duplicate Key Detection")
    class DuplicateKeyValidation {

        @Test
        @DisplayName("Product duplicate key throws DuplicateKeyException")
        void validateProductDuplicateKey() {
            ProductDto dto = ProductDto.builder()
                    .code("DUP01")
                    .name("First Product")
                    .price(new BigDecimal("100.00"))
                    .stock(50)
                    .build();

            productService.createProduct(dto);

            ProductDto duplicate = ProductDto.builder()
                    .code("DUP01")
                    .name("Duplicate Product")
                    .price(new BigDecimal("200.00"))
                    .stock(30)
                    .build();

            DuplicateKeyException exception = assertThrows(
                    DuplicateKeyException.class,
                    () -> productService.createProduct(duplicate)
            );

            assertEquals("Product", exception.getEntityType());
            assertEquals("DUP01", exception.getKey());
            assertTrue(exception.getMessage().contains("Duplicate"));
            assertTrue(exception.getMessage().contains("DUP01"));
        }

        @Test
        @DisplayName("Customer duplicate key throws DuplicateKeyException")
        void validateCustomerDuplicateKey() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("CUST001")
                    .name("First Customer")
                    .phone("555-0001")
                    .build();

            customerService.addCustomer(dto);

            CustomerDto duplicate = CustomerDto.builder()
                    .customerId("CUST001")
                    .name("Duplicate Customer")
                    .phone("555-0002")
                    .build();

            DuplicateKeyException exception = assertThrows(
                    DuplicateKeyException.class,
                    () -> customerService.addCustomer(duplicate)
            );

            assertEquals("Customer", exception.getEntityType());
            assertEquals("CUST001", exception.getKey());
        }

        @Test
        @DisplayName("DuplicateKeyException provides entity type and key")
        void validateDuplicateKeyExceptionDetails() {
            ProductDto dto = ProductDto.builder()
                    .code("TEST1")
                    .name("Test")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            productService.createProduct(dto);

            try {
                productService.createProduct(dto);
                fail("Should throw DuplicateKeyException");
            } catch (DuplicateKeyException e) {
                assertNotNull(e.getEntityType());
                assertNotNull(e.getKey());
                assertNotNull(e.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("FILE STATUS 23 - Record Not Found")
    class RecordNotFoundValidation {

        @Test
        @DisplayName("Product not found throws RecordNotFoundException")
        void validateProductNotFound() {
            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> productService.findByCode("NOTEX")
            );

            assertEquals("Product", exception.getEntityType());
            assertEquals("NOTEX", exception.getKey());
            assertTrue(exception.getMessage().contains("not found"));
        }

        @Test
        @DisplayName("Customer not found throws RecordNotFoundException")
        void validateCustomerNotFound() {
            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> customerService.findByCustomerId("NOTEXIST")
            );

            assertEquals("Customer", exception.getEntityType());
            assertEquals("NOTEXIST", exception.getKey());
        }

        @Test
        @DisplayName("Sale with non-existent product throws RecordNotFoundException")
        void validateSaleProductNotFound() {
            SaleDto dto = SaleDto.builder()
                    .productCode("NOTEX")
                    .quantity(5)
                    .build();

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> salesService.createSale(dto)
            );

            assertEquals("Product", exception.getEntityType());
            assertEquals("NOTEX", exception.getKey());
        }

        @Test
        @DisplayName("Update non-existent product throws RecordNotFoundException")
        void validateUpdateProductNotFound() {
            ProductDto updateDto = ProductDto.builder()
                    .name("Updated")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            assertThrows(RecordNotFoundException.class,
                    () -> productService.updateProduct("NOTEX", updateDto));
        }

        @Test
        @DisplayName("Delete non-existent product throws RecordNotFoundException")
        void validateDeleteProductNotFound() {
            assertThrows(RecordNotFoundException.class,
                    () -> productService.deleteProduct("NOTEX"));
        }

        @Test
        @DisplayName("Update non-existent customer throws RecordNotFoundException")
        void validateUpdateCustomerNotFound() {
            CustomerDto updateDto = CustomerDto.builder()
                    .name("Updated")
                    .phone("555-0000")
                    .build();

            assertThrows(RecordNotFoundException.class,
                    () -> customerService.updateCustomer("NOTEX", updateDto));
        }

        @Test
        @DisplayName("Delete non-existent customer throws RecordNotFoundException")
        void validateDeleteCustomerNotFound() {
            assertThrows(RecordNotFoundException.class,
                    () -> customerService.deleteCustomer("NOTEX"));
        }

        @Test
        @DisplayName("RecordNotFoundException provides entity type and key")
        void validateRecordNotFoundExceptionDetails() {
            try {
                productService.findByCode("MISSING");
                fail("Should throw RecordNotFoundException");
            } catch (RecordNotFoundException e) {
                assertNotNull(e.getEntityType());
                assertNotNull(e.getKey());
                assertNotNull(e.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Insufficient Stock - Enhanced Error Handling")
    class InsufficientStockValidation {

        @BeforeEach
        void setUp() {
            productService.initializeProducts();
        }

        @Test
        @DisplayName("Insufficient stock is recorded as failure in batch processing")
        void validateInsufficientStock() {
            SaleDto dto = SaleDto.builder()
                    .productCode("00001")
                    .quantity(1000)
                    .build();

            salesService.createSale(dto);

            BatchProcessingResult result = salesService.updateInventoryFromSales();
            
            assertEquals(1, result.getFailureCount(),
                    "Insufficient stock should be recorded as failure");
            assertEquals(0, result.getSuccessCount(),
                    "No sales should succeed with insufficient stock");
            assertTrue(result.getErrors().get(0).contains("Insufficient stock"),
                    "Failure message should mention insufficient stock");
        }

        @Test
        @DisplayName("Product reduceStock throws IllegalArgumentException for insufficient stock")
        void validateProductReduceStock() {
            ProductDto dto = ProductDto.builder()
                    .code("STOCK")
                    .name("Stock Test")
                    .price(BigDecimal.ONE)
                    .stock(10)
                    .build();

            productService.createProduct(dto);

            var product = productService.getProductEntity("STOCK");

            assertThrows(IllegalArgumentException.class,
                    () -> product.reduceStock(100));
        }

        @Test
        @DisplayName("Exact stock amount can be reduced")
        void validateExactStockReduction() {
            ProductDto dto = ProductDto.builder()
                    .code("EXACT")
                    .name("Exact Stock")
                    .price(BigDecimal.ONE)
                    .stock(10)
                    .build();

            productService.createProduct(dto);

            var product = productService.getProductEntity("EXACT");

            assertDoesNotThrow(() -> product.reduceStock(10));
            assertEquals(0, product.getStock());
        }
    }

    @Nested
    @DisplayName("FILE STATUS 00 - Success Cases")
    class SuccessCasesValidation {

        @Test
        @DisplayName("Successful product creation returns product")
        void validateSuccessfulProductCreation() {
            ProductDto dto = ProductDto.builder()
                    .code("SUCC1")
                    .name("Success Test")
                    .price(new BigDecimal("99.99"))
                    .stock(100)
                    .build();

            ProductDto created = productService.createProduct(dto);

            assertNotNull(created);
            assertEquals("SUCC1", created.getCode());
        }

        @Test
        @DisplayName("Successful customer creation returns customer")
        void validateSuccessfulCustomerCreation() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("SUCC001")
                    .name("Success Customer")
                    .phone("555-0000")
                    .build();

            CustomerDto created = customerService.addCustomer(dto);

            assertNotNull(created);
            assertEquals("SUCC001", created.getCustomerId());
        }

        @Test
        @DisplayName("Successful product lookup returns product")
        void validateSuccessfulProductLookup() {
            ProductDto dto = ProductDto.builder()
                    .code("LOOK1")
                    .name("Lookup Test")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            productService.createProduct(dto);

            ProductDto found = productService.findByCode("LOOK1");

            assertNotNull(found);
            assertEquals("LOOK1", found.getCode());
        }

        @Test
        @DisplayName("Successful customer lookup returns customer")
        void validateSuccessfulCustomerLookup() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("LOOK001")
                    .name("Lookup Customer")
                    .phone("555-0000")
                    .build();

            customerService.addCustomer(dto);

            CustomerDto found = customerService.findByCustomerId("LOOK001");

            assertNotNull(found);
            assertEquals("LOOK001", found.getCustomerId());
        }
    }

    @Nested
    @DisplayName("Exception Message Quality")
    class ExceptionMessageValidation {

        @Test
        @DisplayName("DuplicateKeyException message is informative")
        void validateDuplicateKeyMessage() {
            ProductDto dto = ProductDto.builder()
                    .code("MSG01")
                    .name("Message Test")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            productService.createProduct(dto);

            try {
                productService.createProduct(dto);
                fail("Should throw exception");
            } catch (DuplicateKeyException e) {
                String message = e.getMessage();
                assertTrue(message.contains("Duplicate") || message.contains("duplicate"));
                assertTrue(message.contains("MSG01"));
            }
        }

        @Test
        @DisplayName("RecordNotFoundException message is informative")
        void validateRecordNotFoundMessage() {
            try {
                productService.findByCode("NOTFOUND");
                fail("Should throw exception");
            } catch (RecordNotFoundException e) {
                String message = e.getMessage();
                assertTrue(message.contains("not found") || message.contains("Not found"));
                assertTrue(message.contains("NOTFOUND"));
            }
        }

        @Test
        @DisplayName("Exception messages include entity type")
        void validateExceptionEntityType() {
            try {
                productService.findByCode("TEST");
            } catch (RecordNotFoundException e) {
                assertTrue(e.getMessage().contains("Product"));
            }

            try {
                customerService.findByCustomerId("TEST");
            } catch (RecordNotFoundException e) {
                assertTrue(e.getMessage().contains("Customer"));
            }
        }
    }

    @Nested
    @DisplayName("Error Recovery Validation")
    class ErrorRecoveryValidation {

        @Test
        @DisplayName("System remains consistent after duplicate key error")
        void validateConsistencyAfterDuplicateError() {
            ProductDto dto = ProductDto.builder()
                    .code("CONS1")
                    .name("Consistency Test")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            productService.createProduct(dto);

            try {
                productService.createProduct(dto);
            } catch (DuplicateKeyException e) {
            }

            ProductDto found = productService.findByCode("CONS1");
            assertEquals("Consistency Test", found.getName());
            assertEquals(1, productService.findAll().stream()
                    .filter(p -> p.getCode().equals("CONS1"))
                    .count());
        }

        @Test
        @DisplayName("System remains consistent after not found error")
        void validateConsistencyAfterNotFoundError() {
            ProductDto dto = ProductDto.builder()
                    .code("CONS2")
                    .name("Consistency Test 2")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            productService.createProduct(dto);

            try {
                productService.findByCode("NOTEXIST");
            } catch (RecordNotFoundException e) {
            }

            ProductDto found = productService.findByCode("CONS2");
            assertEquals("Consistency Test 2", found.getName());
        }

        @Test
        @DisplayName("Multiple operations can continue after error")
        void validateContinuedOperationsAfterError() {
            ProductDto dto1 = ProductDto.builder()
                    .code("MULT1")
                    .name("Multi Test 1")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            productService.createProduct(dto1);

            try {
                productService.createProduct(dto1);
            } catch (DuplicateKeyException e) {
            }

            ProductDto dto2 = ProductDto.builder()
                    .code("MULT2")
                    .name("Multi Test 2")
                    .price(BigDecimal.ONE)
                    .stock(1)
                    .build();

            ProductDto created = productService.createProduct(dto2);
            assertNotNull(created);
            assertEquals("MULT2", created.getCode());
        }
    }
}
