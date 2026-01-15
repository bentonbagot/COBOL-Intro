package com.cobolmodernization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cobolmodernization.entity.Customer;
import com.cobolmodernization.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.addCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @GetMapping("/{key}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String key) {
        return customerService.findByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{key}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String key,
            @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(key, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String key) {
        customerService.deleteCustomer(key);
        return ResponseEntity.noContent().build();
    }
}
