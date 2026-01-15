package com.cobolmodernization.controller;

import com.cobolmodernization.dto.CustomerDto;
import com.cobolmodernization.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Customer operations.
 * Transforms COBOL interactive customer management into REST API endpoints.
 * 
 * COBOL Program Mapping (from EJEMPLO-INDEXADO.cbl / EXAMPLE-INDEX.cbl):
 * - 'A' (Agregar/Add) -> POST /api/customers
 * - 'B' (Buscar/Search) -> GET /api/customers/{id}
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Add a new customer.
     * Translated from COBOL 'A' option (Agregar).
     */
    @PostMapping
    public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto dto) {
        CustomerDto created = customerService.addCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Search customer by ID.
     * Translated from COBOL 'B' option (Buscar).
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable String customerId) {
        CustomerDto customer = customerService.findByCustomerId(customerId);
        return ResponseEntity.ok(customer);
    }

    /**
     * Get all customers.
     */
    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    /**
     * Search customers by name pattern.
     * Enhanced search capability beyond original COBOL.
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchByName(@RequestParam String name) {
        List<CustomerDto> customers = customerService.searchByName(name);
        return ResponseEntity.ok(customers);
    }

    /**
     * Update an existing customer.
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerDto dto) {
        CustomerDto updated = customerService.updateCustomer(customerId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a customer.
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if customer exists.
     */
    @GetMapping("/{customerId}/exists")
    public ResponseEntity<Boolean> customerExists(@PathVariable String customerId) {
        boolean exists = customerService.existsByCustomerId(customerId);
        return ResponseEntity.ok(exists);
    }
}
