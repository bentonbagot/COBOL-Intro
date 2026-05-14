package com.cobolintro;

import com.cobolintro.dao.CustomerDao;
import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.model.Customer;
import com.cobolintro.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    private CustomerDao customerDao;
    private CustomerService customerService;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager.initializeDatabase();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM customers");
        }
        customerDao = new CustomerDao();
        customerService = new CustomerService(customerDao);
    }

    @Test
    @DisplayName("Add a customer and verify fields via search")
    void testAddCustomer() {
        Customer customer = new Customer("C001", "Alice Smith", "555-0100");
        customerService.addCustomer(customer);

        Customer found = customerService.searchCustomer("C001");
        assertNotNull(found);
        assertEquals("C001", found.getKey());
        assertEquals("Alice Smith", found.getName());
        assertEquals("555-0100", found.getPhone());
    }

    @Test
    @DisplayName("Adding a duplicate customer is handled gracefully with only one record stored")
    void testAddDuplicateCustomer() {
        Customer customer = new Customer("C002", "Bob Jones", "555-0200");
        customerService.addCustomer(customer);
        customerService.addCustomer(customer);

        List<Customer> all = customerDao.findAll();
        long count = all.stream().filter(c -> "C002".equals(c.getKey())).count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Search for an existing customer returns correct data")
    void testSearchCustomer() {
        Customer customer = new Customer("C003", "Carol White", "555-0300");
        customerService.addCustomer(customer);

        Customer found = customerService.searchCustomer("C003");
        assertNotNull(found);
        assertEquals("C003", found.getKey());
        assertEquals("Carol White", found.getName());
        assertEquals("555-0300", found.getPhone());
    }

    @Test
    @DisplayName("Search for a non-existent customer returns null")
    void testSearchNonExistentCustomer() {
        Customer found = customerService.searchCustomer("NOKEY");
        assertNull(found);
    }
}
