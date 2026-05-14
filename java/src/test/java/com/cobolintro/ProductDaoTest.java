package com.cobolintro;

import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.dao.ProductDao;
import com.cobolintro.exception.DuplicateKeyException;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoTest {

    private ProductDao productDao;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager.initializeDatabase();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM sales");
            stmt.execute("DELETE FROM products");
        }
        productDao = new ProductDao();
    }

    @Test
    @DisplayName("testCreate — create a product, find it by code, verify all fields")
    void testCreate() {
        Product p = new Product("A0001", "Test Product", new BigDecimal("19.99"), 100);
        productDao.create(p);

        Product found = productDao.findByCode("A0001");
        assertNotNull(found);
        assertEquals("A0001", found.getCode());
        assertEquals("Test Product", found.getName());
        assertEquals(new BigDecimal("19.99"), found.getPrice());
        assertEquals(100, found.getStock());
    }

    @Test
    @DisplayName("testCreateDuplicate — create same product twice, assert DuplicateKeyException")
    void testCreateDuplicate() {
        Product p = new Product("A0001", "Test Product", new BigDecimal("19.99"), 100);
        productDao.create(p);

        assertThrows(DuplicateKeyException.class, () -> productDao.create(p));
    }

    @Test
    @DisplayName("testFindByCode — create product, find by code, verify fields including BigDecimal price")
    void testFindByCode() {
        Product p = new Product("B0002", "Widget", new BigDecimal("49.95"), 250);
        productDao.create(p);

        Product found = productDao.findByCode("B0002");
        assertNotNull(found);
        assertEquals("B0002", found.getCode());
        assertEquals("Widget", found.getName());
        assertEquals(new BigDecimal("49.95"), found.getPrice());
        assertEquals(250, found.getStock());
    }

    @Test
    @DisplayName("testFindByCodeNotFound — find non-existent code, assert RecordNotFoundException")
    void testFindByCodeNotFound() {
        assertThrows(RecordNotFoundException.class, () -> productDao.findByCode("ZZZZZ"));
    }

    @Test
    @DisplayName("testUpdate — create product, update its stock/price, find again and verify changes")
    void testUpdate() {
        Product p = new Product("C0003", "Gadget", new BigDecimal("10.00"), 50);
        productDao.create(p);

        p.setPrice(new BigDecimal("15.50"));
        p.setStock(75);
        productDao.update(p);

        Product updated = productDao.findByCode("C0003");
        assertEquals(new BigDecimal("15.50"), updated.getPrice());
        assertEquals(75, updated.getStock());
    }

    @Test
    @DisplayName("testFindAll — create multiple products, findAll and verify count")
    void testFindAll() {
        productDao.create(new Product("D0001", "Item A", new BigDecimal("5.00"), 10));
        productDao.create(new Product("D0002", "Item B", new BigDecimal("6.00"), 20));
        productDao.create(new Product("D0003", "Item C", new BigDecimal("7.00"), 30));

        List<Product> all = productDao.findAll();
        assertEquals(3, all.size());
    }
}
