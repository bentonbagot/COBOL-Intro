package com.cobolmodernization.service;

import com.cobolmodernization.dto.CustomerDto;
import com.cobolmodernization.entity.Customer;
import com.cobolmodernization.exception.DuplicateKeyException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Customer operations.
 * Translates business logic from COBOL program:
 * - EJEMPLO-INDEXADO.cbl / EXAMPLE-INDEX.cbl - Customer add/search operations
 * 
 * COBOL Operations Mapping:
 * - 'A' (Agregar/Add) -> addCustomer()
 * - 'B' (Buscar/Search) -> findByCustomerId()
 * - INVALID KEY (duplicate) -> DuplicateKeyException
 * - INVALID KEY (not found) -> RecordNotFoundException
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Add a new customer.
     * Translated from COBOL 'A' option in EJEMPLO-INDEXADO.cbl:
     *   WRITE REGISTRO-CLIENTE INVALID KEY
     *       DISPLAY "Clave duplicada"
     * 
     * @param dto the customer data
     * @return the created customer
     * @throws DuplicateKeyException if customer ID already exists
     */
    @Transactional
    public CustomerDto addCustomer(CustomerDto dto) {
        log.info("Adding customer with ID: {}", dto.getCustomerId());
        
        if (customerRepository.existsByCustomerId(dto.getCustomerId())) {
            log.warn("Duplicate customer key: {}", dto.getCustomerId());
            throw new DuplicateKeyException("Customer", dto.getCustomerId());
        }

        Customer customer = Customer.builder()
                .customerId(dto.getCustomerId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Customer successfully added: {}", saved.getCustomerId());
        
        return toDto(saved);
    }

    /**
     * Search customer by ID.
     * Translated from COBOL 'B' option in EJEMPLO-INDEXADO.cbl:
     *   READ ARCHIVO-CLIENTES
     *       INVALID KEY DISPLAY "No encontrado"
     *       NOT INVALID DISPLAY NOMBRE-CLIENTE " - " TELEFONO
     * 
     * @param customerId the customer ID to search
     * @return the customer if found
     * @throws RecordNotFoundException if customer not found
     */
    @Transactional(readOnly = true)
    public CustomerDto findByCustomerId(String customerId) {
        log.info("Searching for customer with ID: {}", customerId);
        
        return customerRepository.findByCustomerId(customerId)
                .map(customer -> {
                    log.info("Customer found: {} - {}", customer.getName(), customer.getPhone());
                    return toDto(customer);
                })
                .orElseThrow(() -> {
                    log.warn("Customer not found: {}", customerId);
                    return new RecordNotFoundException("Customer", customerId);
                });
    }

    /**
     * Get all customers.
     * 
     * @return list of all customers
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> findAll() {
        log.info("Retrieving all customers");
        return customerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Search customers by name pattern.
     * Enhanced search capability beyond original COBOL.
     * 
     * @param namePart the name pattern to search
     * @return list of matching customers
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> searchByName(String namePart) {
        log.info("Searching customers by name: {}", namePart);
        return customerRepository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing customer.
     * 
     * @param customerId the customer ID
     * @param dto the updated customer data
     * @return the updated customer
     * @throws RecordNotFoundException if customer not found
     */
    @Transactional
    public CustomerDto updateCustomer(String customerId, CustomerDto dto) {
        log.info("Updating customer with ID: {}", customerId);
        
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RecordNotFoundException("Customer", customerId));

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());

        Customer saved = customerRepository.save(customer);
        log.info("Customer updated: {}", saved.getCustomerId());
        
        return toDto(saved);
    }

    /**
     * Delete a customer.
     * 
     * @param customerId the customer ID
     * @throws RecordNotFoundException if customer not found
     */
    @Transactional
    public void deleteCustomer(String customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        
        if (!customerRepository.existsByCustomerId(customerId)) {
            throw new RecordNotFoundException("Customer", customerId);
        }
        
        customerRepository.deleteById(customerId);
        log.info("Customer deleted: {}", customerId);
    }

    /**
     * Check if customer exists.
     * 
     * @param customerId the customer ID
     * @return true if customer exists
     */
    @Transactional(readOnly = true)
    public boolean existsByCustomerId(String customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }

    private CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .build();
    }
}
