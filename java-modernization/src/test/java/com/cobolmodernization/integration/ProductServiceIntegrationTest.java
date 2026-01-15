package com.cobolmodernization.integration;

import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.exception.DuplicateKeyException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductService.
 * Validates that Java implementation produces identical results to COBOL createdat.cbl.
 * 
 * COBOL Behavior Being Tested:
 * - Product creation with duplicate detection (file status 22)
 * - Product lookup by code (file status 23 for not found)
 * - Sequential read of all products
 */
@SpringBootTest
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Create product - equivalent to COBOL WRITE PRODUCTS-RECORD")
    void testCreateProduct() {
        ProductDto dto = ProductDto.builder()
                .code("00001")
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(50)
                .build();

        ProductDto created = productService.createProduct(dto);

        assertNotNull(created);
        assertEquals("00001", created.getCode());
        assertEquals("Test Product", created.getName());
        assertEquals(new BigDecimal("100.00"), created.getPrice());
        assertEquals(50, created.getStock());
    }

    @Test
    @DisplayName("Duplicate key detection - equivalent to COBOL file status 22")
    void testDuplicateKeyDetection() {
        ProductDto dto = ProductDto.builder()
                .code("00001")
                .name("Product A")
                .price(new BigDecimal("100.00"))
                .stock(50)
                .build();

        productService.createProduct(dto);

        ProductDto duplicate = ProductDto.builder()
                .code("00001")
                .name("Product B")
                .price(new BigDecimal("200.00"))
                .stock(30)
                .build();

        assertThrows(DuplicateKeyException.class, () -> productService.createProduct(duplicate));
    }

    @Test
    @DisplayName("Find product by code - equivalent to COBOL READ ... KEY IS")
    void testFindByCode() {
        ProductDto dto = ProductDto.builder()
                .code("00002")
                .name("Test Product")
                .price(new BigDecimal("150.00"))
                .stock(25)
                .build();

        productService.createProduct(dto);

        ProductDto found = productService.findByCode("00002");

        assertNotNull(found);
        assertEquals("00002", found.getCode());
        assertEquals("Test Product", found.getName());
    }

    @Test
    @DisplayName("Product not found - equivalent to COBOL file status 23")
    void testProductNotFound() {
        assertThrows(RecordNotFoundException.class, () -> productService.findByCode("99999"));
    }

    @Test
    @DisplayName("Initialize products - equivalent to COBOL CREATE-PRODUCTS predefined data")
    void testInitializeProducts() {
        productService.initializeProducts();

        List<ProductDto> products = productService.findAll();

        assertEquals(3, products.size());
        assertTrue(productService.existsByCode("00001"));
        assertTrue(productService.existsByCode("00002"));
        assertTrue(productService.existsByCode("00003"));
    }

    @Test
    @DisplayName("Product data matches COBOL field sizes")
    void testFieldSizeConstraints() {
        ProductDto dto = ProductDto.builder()
                .code("12345")
                .name("12345678901234567890")
                .price(new BigDecimal("9999999.99"))
                .stock(99999)
                .build();

        ProductDto created = productService.createProduct(dto);

        assertEquals(5, created.getCode().length());
        assertEquals(20, created.getName().length());
    }

    @Test
    @DisplayName("Update product - equivalent to COBOL REWRITE")
    void testUpdateProduct() {
        ProductDto dto = ProductDto.builder()
                .code("00001")
                .name("Original Name")
                .price(new BigDecimal("100.00"))
                .stock(50)
                .build();

        productService.createProduct(dto);

        ProductDto updateDto = ProductDto.builder()
                .name("Updated Name")
                .price(new BigDecimal("150.00"))
                .stock(75)
                .build();

        ProductDto updated = productService.updateProduct("00001", updateDto);

        assertEquals("Updated Name", updated.getName());
        assertEquals(new BigDecimal("150.00"), updated.getPrice());
        assertEquals(75, updated.getStock());
    }

    @Test
    @DisplayName("Delete product - equivalent to COBOL DELETE")
    void testDeleteProduct() {
        ProductDto dto = ProductDto.builder()
                .code("00001")
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(50)
                .build();

        productService.createProduct(dto);
        assertTrue(productService.existsByCode("00001"));

        productService.deleteProduct("00001");
        assertFalse(productService.existsByCode("00001"));
    }
}
