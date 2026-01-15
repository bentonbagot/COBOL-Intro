package com.cobolmodernization.validation;

import com.cobolmodernization.dto.CustomerDto;
import com.cobolmodernization.exception.DuplicateKeyException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for Customer data consistency.
 * Validates that Java implementation produces identical results to COBOL programs:
 * - EXAMPLE-INDEX.cob / EJEMPLO-INDEXADO.cbl - Interactive customer add/search operations
 * 
 * Tests verify:
 * 1. Data structure matches COBOL REGISTRO-CLIENTE (PIC clauses)
 * 2. Add operation matches COBOL 'A' option (Agregar)
 * 3. Search operation matches COBOL 'B' option (Buscar)
 * 4. Error handling matches COBOL INVALID KEY responses
 */
@SpringBootTest
@Transactional
class CustomerDataValidationTest {

    @Autowired
    private CustomerService customerService;

    @Nested
    @DisplayName("EXAMPLE-INDEX.cob Validation - Customer Add Operation ('A' option)")
    class AddCustomerValidation {

        @Test
        @DisplayName("Add customer matches COBOL WRITE REGISTRO-CLIENTE")
        void validateAddCustomer() {
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
        @DisplayName("Duplicate key detection matches COBOL 'Clave duplicada' message")
        void validateDuplicateKeyDetection() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("DUP001")
                    .name("First Customer")
                    .phone("555-0001")
                    .build();

            customerService.addCustomer(dto);

            CustomerDto duplicate = CustomerDto.builder()
                    .customerId("DUP001")
                    .name("Duplicate Customer")
                    .phone("555-0002")
                    .build();

            DuplicateKeyException exception = assertThrows(
                    DuplicateKeyException.class,
                    () -> customerService.addCustomer(duplicate)
            );

            assertEquals("Customer", exception.getEntityType());
            assertEquals("DUP001", exception.getKey());
        }

        @Test
        @DisplayName("Add multiple customers")
        void validateAddMultipleCustomers() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("CUST001")
                    .name("Customer One")
                    .phone("555-0001")
                    .build());

            customerService.addCustomer(CustomerDto.builder()
                    .customerId("CUST002")
                    .name("Customer Two")
                    .phone("555-0002")
                    .build());

            customerService.addCustomer(CustomerDto.builder()
                    .customerId("CUST003")
                    .name("Customer Three")
                    .phone("555-0003")
                    .build());

            List<CustomerDto> customers = customerService.findAll();
            assertEquals(3, customers.size());
        }
    }

    @Nested
    @DisplayName("EXAMPLE-INDEX.cob Validation - Customer Search Operation ('B' option)")
    class SearchCustomerValidation {

        @Test
        @DisplayName("Search customer by ID matches COBOL READ ARCHIVO-CLIENTES")
        void validateSearchCustomerById() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("SRCH001")
                    .name("Search Test Customer")
                    .phone("555-9999")
                    .build());

            CustomerDto found = customerService.findByCustomerId("SRCH001");

            assertNotNull(found);
            assertEquals("SRCH001", found.getCustomerId());
            assertEquals("Search Test Customer", found.getName());
            assertEquals("555-9999", found.getPhone());
        }

        @Test
        @DisplayName("Not found matches COBOL 'No encontrado' message")
        void validateCustomerNotFound() {
            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> customerService.findByCustomerId("NOTEXIST")
            );

            assertEquals("Customer", exception.getEntityType());
            assertEquals("NOTEXIST", exception.getKey());
        }

        @Test
        @DisplayName("Search returns correct customer data - matches COBOL DISPLAY output")
        void validateSearchReturnsCorrectData() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("DATA001")
                    .name("Data Validation Customer")
                    .phone("123-456-7890")
                    .build());

            CustomerDto found = customerService.findByCustomerId("DATA001");

            assertEquals("DATA001", found.getCustomerId());
            assertEquals("Data Validation Customer", found.getName());
            assertEquals("123-456-7890", found.getPhone());
        }
    }

    @Nested
    @DisplayName("COBOL Data Structure Validation - REGISTRO-CLIENTE PIC Clauses")
    class DataStructureValidation {

        @Test
        @DisplayName("Customer ID matches COBOL PIC X(10)")
        void validateCustomerIdLength() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("1234567890")
                    .name("Test")
                    .phone("555-0000")
                    .build();

            CustomerDto created = customerService.addCustomer(dto);
            assertEquals(10, created.getCustomerId().length());
        }

        @Test
        @DisplayName("Customer name matches COBOL PIC X(30)")
        void validateCustomerNameLength() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("NAME001")
                    .name("123456789012345678901234567890")
                    .phone("555-0000")
                    .build();

            CustomerDto created = customerService.addCustomer(dto);
            assertEquals(30, created.getName().length());
        }

        @Test
        @DisplayName("Customer phone matches COBOL PIC X(15)")
        void validateCustomerPhoneLength() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("PHONE001")
                    .name("Phone Test")
                    .phone("123456789012345")
                    .build();

            CustomerDto created = customerService.addCustomer(dto);
            assertEquals(15, created.getPhone().length());
        }

        @Test
        @DisplayName("Customer with shorter fields - padding not required in Java")
        void validateShorterFields() {
            CustomerDto dto = CustomerDto.builder()
                    .customerId("SHORT")
                    .name("Short Name")
                    .phone("555")
                    .build();

            CustomerDto created = customerService.addCustomer(dto);

            assertEquals("SHORT", created.getCustomerId());
            assertEquals("Short Name", created.getName());
            assertEquals("555", created.getPhone());
        }
    }

    @Nested
    @DisplayName("Enhanced Operations - Beyond COBOL Functionality")
    class EnhancedOperationsValidation {

        @Test
        @DisplayName("Search by name - enhanced capability")
        void validateSearchByName() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("CUST001")
                    .name("John Doe")
                    .phone("555-0001")
                    .build());

            customerService.addCustomer(CustomerDto.builder()
                    .customerId("CUST002")
                    .name("Jane Doe")
                    .phone("555-0002")
                    .build());

            customerService.addCustomer(CustomerDto.builder()
                    .customerId("CUST003")
                    .name("Bob Smith")
                    .phone("555-0003")
                    .build());

            List<CustomerDto> doeCustomers = customerService.searchByName("Doe");
            assertEquals(2, doeCustomers.size());

            List<CustomerDto> smithCustomers = customerService.searchByName("Smith");
            assertEquals(1, smithCustomers.size());
        }

        @Test
        @DisplayName("Get all customers - sequential read")
        void validateGetAllCustomers() {
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
        void validateUpdateCustomer() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("UPD001")
                    .name("Original Name")
                    .phone("555-0000")
                    .build());

            CustomerDto updateDto = CustomerDto.builder()
                    .name("Updated Name")
                    .phone("555-1111")
                    .build();

            CustomerDto updated = customerService.updateCustomer("UPD001", updateDto);

            assertEquals("UPD001", updated.getCustomerId());
            assertEquals("Updated Name", updated.getName());
            assertEquals("555-1111", updated.getPhone());
        }

        @Test
        @DisplayName("Update non-existent customer throws exception")
        void validateUpdateNotFound() {
            CustomerDto updateDto = CustomerDto.builder()
                    .name("Test")
                    .phone("555-0000")
                    .build();

            assertThrows(RecordNotFoundException.class,
                    () -> customerService.updateCustomer("NOTEXIST", updateDto));
        }

        @Test
        @DisplayName("Delete customer")
        void validateDeleteCustomer() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("DEL001")
                    .name("To Delete")
                    .phone("555-0000")
                    .build());

            assertTrue(customerService.existsByCustomerId("DEL001"));

            customerService.deleteCustomer("DEL001");

            assertFalse(customerService.existsByCustomerId("DEL001"));
        }

        @Test
        @DisplayName("Delete non-existent customer throws exception")
        void validateDeleteNotFound() {
            assertThrows(RecordNotFoundException.class,
                    () -> customerService.deleteCustomer("NOTEXIST"));
        }

        @Test
        @DisplayName("Check customer exists")
        void validateExistsCheck() {
            assertFalse(customerService.existsByCustomerId("CHECK01"));

            customerService.addCustomer(CustomerDto.builder()
                    .customerId("CHECK01")
                    .name("Exists Check")
                    .phone("555-0000")
                    .build());

            assertTrue(customerService.existsByCustomerId("CHECK01"));
        }
    }

    @Nested
    @DisplayName("COBOL Interactive Menu Simulation")
    class InteractiveMenuSimulation {

        @Test
        @DisplayName("Simulate COBOL menu flow: Add -> Search -> Add -> Search")
        void validateMenuFlow() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("MENU001")
                    .name("First Customer")
                    .phone("555-0001")
                    .build());

            CustomerDto found1 = customerService.findByCustomerId("MENU001");
            assertEquals("First Customer", found1.getName());

            customerService.addCustomer(CustomerDto.builder()
                    .customerId("MENU002")
                    .name("Second Customer")
                    .phone("555-0002")
                    .build());

            CustomerDto found2 = customerService.findByCustomerId("MENU002");
            assertEquals("Second Customer", found2.getName());

            CustomerDto found1Again = customerService.findByCustomerId("MENU001");
            assertEquals("First Customer", found1Again.getName());
        }

        @Test
        @DisplayName("Simulate COBOL error handling: Add duplicate, Search not found")
        void validateErrorHandlingFlow() {
            customerService.addCustomer(CustomerDto.builder()
                    .customerId("ERR001")
                    .name("Error Test")
                    .phone("555-0000")
                    .build());

            assertThrows(DuplicateKeyException.class, () ->
                    customerService.addCustomer(CustomerDto.builder()
                            .customerId("ERR001")
                            .name("Duplicate")
                            .phone("555-1111")
                            .build()));

            assertThrows(RecordNotFoundException.class, () ->
                    customerService.findByCustomerId("NOTFOUND"));

            CustomerDto found = customerService.findByCustomerId("ERR001");
            assertEquals("Error Test", found.getName());
        }
    }
}
