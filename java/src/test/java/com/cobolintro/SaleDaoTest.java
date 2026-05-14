package com.cobolintro;

import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.dao.SaleDao;
import com.cobolintro.model.Sale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleDaoTest {

    private SaleDao saleDao;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager.initializeDatabase();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM sales");
        }
        saleDao = new SaleDao();
    }

    @Test
    @DisplayName("testWrite — write a sale, readAll and verify it's there")
    void testWrite() {
        saleDao.write(new Sale("P0001", 5));

        List<Sale> sales = saleDao.readAll();
        assertEquals(1, sales.size());
        assertEquals("P0001", sales.get(0).getProductCode());
        assertEquals(5, sales.get(0).getQuantity());
    }

    @Test
    @DisplayName("testReadAll — write multiple sales, readAll and verify count and contents")
    void testReadAll() {
        saleDao.write(new Sale("P0001", 3));
        saleDao.write(new Sale("P0002", 7));
        saleDao.write(new Sale("P0003", 12));

        List<Sale> sales = saleDao.readAll();
        assertEquals(3, sales.size());
        assertEquals("P0001", sales.get(0).getProductCode());
        assertEquals(3, sales.get(0).getQuantity());
        assertEquals("P0002", sales.get(1).getProductCode());
        assertEquals(7, sales.get(1).getQuantity());
        assertEquals("P0003", sales.get(2).getProductCode());
        assertEquals(12, sales.get(2).getQuantity());
    }

    @Test
    @DisplayName("testCount — write sales, verify count() matches")
    void testCount() {
        assertEquals(0, saleDao.count());

        saleDao.write(new Sale("P0001", 2));
        saleDao.write(new Sale("P0002", 4));
        assertEquals(2, saleDao.count());

        saleDao.write(new Sale("P0003", 6));
        assertEquals(3, saleDao.count());
    }
}
