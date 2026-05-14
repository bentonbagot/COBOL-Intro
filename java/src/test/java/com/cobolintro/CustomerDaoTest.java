package com.cobolintro;

import com.cobolintro.dao.CustomerDao;
import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.exception.DuplicateKeyException;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDaoTest {

    private CustomerDao customerDao;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager.initializeDatabase();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM customers");
        }
        customerDao = new CustomerDao();
    }

    @Test
    @DisplayName("testAdd — add a customer, findByKey and verify")
    void testAdd() {
        Customer c = new Customer("CUST001", "Alice Smith", "555-1234");
        customerDao.add(c);

        Customer found = customerDao.findByKey("CUST001");
        assertNotNull(found);
        assertEquals("CUST001", found.getKey());
        assertEquals("Alice Smith", found.getName());
        assertEquals("555-1234", found.getPhone());
    }

    @Test
    @DisplayName("testAddDuplicate — add same key twice, assert DuplicateKeyException")
    void testAddDuplicate() {
        Customer c = new Customer("CUST002", "Bob Jones", "555-5678");
        customerDao.add(c);

        assertThrows(DuplicateKeyException.class, () ->
                customerDao.add(new Customer("CUST002", "Bob Duplicate", "555-0000")));
    }

    @Test
    @DisplayName("testFindByKey — add customer, find and verify all fields")
    void testFindByKey() {
        Customer c = new Customer("CUST003", "Carol White", "555-9012");
        customerDao.add(c);

        Customer found = customerDao.findByKey("CUST003");
        assertNotNull(found);
        assertEquals("CUST003", found.getKey());
        assertEquals("Carol White", found.getName());
        assertEquals("555-9012", found.getPhone());
    }

    @Test
    @DisplayName("testFindByKeyNotFound — find non-existent, assert RecordNotFoundException")
    void testFindByKeyNotFound() {
        assertThrows(RecordNotFoundException.class, () -> customerDao.findByKey("NOEXIST"));
    }
}
