package com.cobolintro;

import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.dao.ProductDao;
import com.cobolintro.dao.SaleDao;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.Product;
import com.cobolintro.service.ProductService;
import com.cobolintro.service.SaleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    private ProductDao productDao;
    private SaleDao saleDao;
    private ProductService productService;
    private SaleService saleService;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager.initializeDatabase();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM sales");
            stmt.execute("DELETE FROM products");
            stmt.execute("DELETE FROM customers");
        }
        productDao = new ProductDao();
        saleDao = new SaleDao();
        productService = new ProductService(productDao);
        saleService = new SaleService(productDao, saleDao);
    }

    @Test
    @DisplayName("testProductToSaleFlow — create product, create sale, verify sale count and product exists")
    void testProductToSaleFlow() {
        Product p = new Product("P0001", "Laptop", new BigDecimal("999.99"), 50);
        productService.createProduct(p);

        int countBefore = saleService.getSaleCount();
        saleService.createSale("P0001", 2);
        int countAfter = saleService.getSaleCount();

        assertEquals(countBefore + 1, countAfter);

        Product found = productService.lookupProduct("P0001");
        assertNotNull(found);
        assertEquals("P0001", found.getCode());
        assertEquals("Laptop", found.getName());
    }

    @Test
    @DisplayName("testInvalidSaleFlow — create sale for non-existent product, verify RecordNotFoundException")
    void testInvalidSaleFlow() {
        assertThrows(RecordNotFoundException.class, () ->
                saleService.createSale("XXXXX", 10));
    }
}
