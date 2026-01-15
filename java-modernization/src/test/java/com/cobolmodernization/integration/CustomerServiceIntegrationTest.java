package com.cobolmodernization.integration;

import com.cobolmodernization.dto.CustomerDto;
import com.cobolmodernization.exception.DuplicateKeyException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CustomerService.
 * Validates that Java implementation produces identical results to COBOL EJEMPLO-INDEXADO.cbl.
 * 
 * COBOL Operations Being Tested:
 * - 'A' (Agregar/Add) - Add customer with duplicate key detection
 * - 'B' (Buscar/Search) - Search customer by key
 */
@SpringBootTest
@Transactional
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Test
    @DisplayName("Add customer - equivalent to COBOL 'A' option (Agregar)")
    void testAddCustomer() {
        CustomerDto dto = CustomerDto.builder()
                .customerId("CUST001")
                .name("John Doe")
                .phone("555-1234")
                .build();

        CustomerDto created = customerService.addCustomer(dto);

        assertNotNull(created);
        assertEquals("CUST001", created.getCustomerId());
        assertEquals("John Doe", created.getName());
        assertEquals("555-1234", created.getPhone());
    }

    @Test
    @DisplayName("Duplicate key detection - equivalent to COBOL 'Clave duplicada'")
    void testDuplicateKeyDetection() {
        CustomerDto dto = CustomerDto.builder()
                .customerId("CUST001")
                .name("John Doe")
                .phone("555-1234")
                .build();

        customerService.addCustomer(dto);

        CustomerDto duplicate = CustomerDto.builder()
                .customerId("CUST001")
                .name("Jane Doe")
                .phone("555-5678")
                .build();

        assertThrows(DuplicateKeyException.class, () -> customerService.addCustomer(duplicate));
    }

    @Test
    @DisplayName("Search customer by ID - equivalent to COBOL 'B' option (Buscar)")
    void testSearchCustomerById() {
        CustomerDto dto = CustomerDto.builder()
                .customerId("CUST002")
                .name("Jane Smith")
                .phone("555-9876")
                .build();

        customerService.addCustomer(dto);

        CustomerDto found = customerService.findByCustomerId("CUST002");

        assertNotNull(found);
        assertEquals("CUST002", found.getCustomerId());
        assertEquals("Jane Smith", found.getName());
        assertEquals("555-9876", found.getPhone());
    }

    @Test
    @DisplayName("Customer not found - equivalent to COBOL 'No encontrado'")
    void testCustomerNotFound() {
        assertThrows(RecordNotFoundException.class, () -> customerService.findByCustomerId("NOTEXIST"));
    }

    @Test
    @DisplayName("Customer data matches COBOL REGISTRO-CLIENTE structure")
    void testFieldSizeConstraints() {
        CustomerDto dto = CustomerDto.builder()
                .customerId("1234567890")
                .name("123456789012345678901234567890")
                .phone("123456789012345")
                .build();

        CustomerDto created = customerService.addCustomer(dto);

        assertEquals(10, created.getCustomerId().length());
        assertEquals(30, created.getName().length());
        assertEquals(15, created.getPhone().length());
    }

    @Test
    @DisplayName("Search customers by name - enhanced capability")
    void testSearchByName() {
        customerService.addCustomer(CustomerDto.builder()
                .customerId("CUST001")
                .name("John Doe")
                .phone("555-1234")
                .build());

        customerService.addCustomer(CustomerDto.builder()
                .customerId("CUST002")
                .name("Jane Doe")
                .phone("555-5678")
                .build());

        customerService.addCustomer(CustomerDto.builder()
                .customerId("CUST003")
                .name("Bob Smith")
                .phone("555-9999")
                .build());

        List<CustomerDto> doeCustomers = customerService.searchByName("Doe");
        assertEquals(2, doeCustomers.size());

        List<CustomerDto> smithCustomers = customerService.searchByName("Smith");
        assertEquals(1, smithCustomers.size());
    }

    @Test
    @DisplayName("Get all customers")
    void testGetAllCustomers() {
        customerService.addCustomer(CustomerDto.builder()
                .customerId("CUST001")
                .name("Customer 1")
                .phone("555-0001")
                .build());

        customerService.addCustomer(CustomerDto.builder()
                .customerId("CUST002")
                .name("Customer 2")
                .phone("555-0002")
                .build());

        List<CustomerDto> customers = customerService.findAll();
        assertEquals(2, customers.size());
    }

    @Test
    @DisplayName("Update customer")
    void testUpdateCustomer() {
        customerService.addCustomer(CustomerDto.builder()
                .customerId("CUST001")
                .name("Original Name")
                .phone("555-0000")
                .build());

        CustomerDto updateDto = CustomerDto.builder()
                .name("Updated Name")
                .phone("555-1111")
                .build();

        CustomerDto updated = customerService.updateCustomer("CUST001", updateDto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("555-1111", updated.getPhone());
    }

    @Test
    @DisplayName("Delete customer")
    void testDeleteCustomer() {
        customerService.addCustomer(CustomerDto.builder()
                .customerId("CUST001")
                .name("To Delete")
                .phone("555-0000")
                .build());

        assertTrue(customerService.existsByCustomerId("CUST001"));

        customerService.deleteCustomer("CUST001");

        assertFalse(customerService.existsByCustomerId("CUST001"));
    }
}
