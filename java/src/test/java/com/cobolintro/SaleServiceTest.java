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

class SaleServiceTest {

    private ProductDao productDao;
    private SaleDao saleDao;
    private SaleService saleService;
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
        saleDao = new SaleDao();
        saleService = new SaleService(productDao, saleDao);
        productService = new ProductService(productDao);
    }

    @Test
    @DisplayName("Create a sale for a valid product and verify count increases")
    void testCreateSaleWithValidProduct() {
        productDao.create(new Product("S0001", "Sale Item", new BigDecimal("5.00"), 10));

        assertEquals(0, saleService.getSaleCount());
        saleService.createSale("S0001", 3);
        assertEquals(1, saleService.getSaleCount());
    }

    @Test
    @DisplayName("Creating a sale for a non-existent product throws RecordNotFoundException")
    void testCreateSaleWithInvalidProduct() {
        assertThrows(RecordNotFoundException.class, () -> saleService.createSale("NOEXIST", 1));
    }

    @Test
    @DisplayName("getSaleCount returns the correct number after multiple sales")
    void testGetSaleCount() {
        productDao.create(new Product("S0002", "Counter Item", new BigDecimal("2.50"), 100));

        saleService.createSale("S0002", 1);
        saleService.createSale("S0002", 2);
        saleService.createSale("S0002", 3);

        assertEquals(3, saleService.getSaleCount());
    }

    @Test
    @DisplayName("generateTestSales creates sales after products are initialized")
    void testGenerateTestSales() {
        productService.initializeProducts();
        saleService.generateTestSales();

        assertTrue(saleService.getSaleCount() > 0);
    }
}
