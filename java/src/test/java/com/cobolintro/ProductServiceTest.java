package com.cobolintro;

import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.dao.ProductDao;
import com.cobolintro.exception.DuplicateKeyException;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.Product;
import com.cobolintro.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductDao productDao;
    private ProductService productService;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager.initializeDatabase();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM sales");
            stmt.execute("DELETE FROM products");
        }
        productDao = new ProductDao();
        productService = new ProductService(productDao);
    }

    @Test
    @DisplayName("Create a product and verify fields match on lookup")
    void testCreateProduct() {
        Product product = new Product("T0001", "Test Widget", new BigDecimal("19.99"), 100);
        productService.createProduct(product);

        Product found = productService.lookupProduct("T0001");
        assertEquals("T0001", found.getCode());
        assertEquals("Test Widget", found.getName());
        assertEquals(new BigDecimal("19.99"), found.getPrice());
        assertEquals(100, found.getStock());
    }

    @Test
    @DisplayName("Creating a duplicate product throws DuplicateKeyException")
    void testCreateDuplicateProduct() {
        Product product = new Product("T0002", "Gadget", new BigDecimal("9.99"), 50);
        productService.createProduct(product);

        Product duplicate = new Product("T0002", "Gadget Copy", new BigDecimal("11.99"), 30);
        assertThrows(DuplicateKeyException.class, () -> productService.createProduct(duplicate));
    }

    @Test
    @DisplayName("Lookup a product by code and verify all fields")
    void testLookupProduct() {
        Product product = new Product("T0003", "Gizmo", new BigDecimal("49.95"), 200);
        productService.createProduct(product);

        Product found = productService.lookupProduct("T0003");
        assertEquals("T0003", found.getCode());
        assertEquals("Gizmo", found.getName());
        assertEquals(new BigDecimal("49.95"), found.getPrice());
        assertEquals(200, found.getStock());
    }

    @Test
    @DisplayName("Lookup a non-existent product throws RecordNotFoundException")
    void testLookupNonExistentProduct() {
        assertThrows(RecordNotFoundException.class, () -> productService.lookupProduct("ZZZZZ"));
    }

    @Test
    @DisplayName("initializeProducts populates products that can be retrieved via findAll")
    void testInitializeProducts() {
        productService.initializeProducts();

        List<Product> all = productDao.findAll();
        assertEquals(7, all.size());
    }

    @Test
    @DisplayName("BigDecimal price arithmetic is exact")
    void testBigDecimalPriceExact() {
        Product product = new Product("T0004", "Precision Item", new BigDecimal("19.99"), 10);
        productService.createProduct(product);

        Product found = productService.lookupProduct("T0004");
        assertEquals(new BigDecimal("19.99"), found.getPrice());
    }
}
