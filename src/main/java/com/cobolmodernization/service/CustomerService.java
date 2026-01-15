package com.cobolmodernization.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cobolmodernization.entity.Customer;
import com.cobolmodernization.exception.CustomerNotFoundException;
import com.cobolmodernization.exception.DuplicateCustomerException;
import com.cobolmodernization.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {
        logger.info("Adding customer with key: {}", customer.getKey());

        if (customerRepository.existsByKey(customer.getKey())) {
            logger.error("Duplicate customer key detected: {}", customer.getKey());
            throw new DuplicateCustomerException(customer.getKey());
        }

        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer successfully registered: {}", savedCustomer.getKey());
        return savedCustomer;
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findByKey(String key) {
        logger.debug("Searching for customer with key: {}", key);
        return customerRepository.findByKey(key);
    }

    @Transactional(readOnly = true)
    public Customer getByKey(String key) {
        return findByKey(key)
                .orElseThrow(() -> new CustomerNotFoundException(key));
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        logger.debug("Retrieving all customers");
        return customerRepository.findAll();
    }

    public Customer updateCustomer(String key, Customer updatedCustomer) {
        logger.info("Updating customer with key: {}", key);

        Customer existingCustomer = getByKey(key);
        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setPhone(updatedCustomer.getPhone());

        Customer savedCustomer = customerRepository.save(existingCustomer);
        logger.info("Customer updated: {}", key);
        return savedCustomer;
    }

    public void deleteCustomer(String key) {
        logger.info("Deleting customer with key: {}", key);
        Customer customer = getByKey(key);
        customerRepository.delete(customer);
        logger.info("Customer deleted: {}", key);
    }

    public boolean existsByKey(String key) {
        return customerRepository.existsByKey(key);
    }

    public long countCustomers() {
        return customerRepository.count();
    }
}
