package com.cobolintro.service;

import com.cobolintro.dao.CustomerDao;
import com.cobolintro.exception.DuplicateKeyException;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.Customer;

/**
 * Service layer for Customer operations.
 * Replaces the ADD and SEARCH flows in EXAMPLE-INDEX.cbl with graceful exception handling.
 */
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    /**
     * Adds a customer via the DAO. If the key already exists, prints a
     * user-friendly message instead of crashing.
     *
     * @param customer the customer to add
     */
    public void addCustomer(Customer customer) {
        try {
            customerDao.add(customer);
        } catch (DuplicateKeyException e) {
            System.out.println("Customer with key " + customer.getKey() + " already exists");
        }
    }

    /**
     * Searches for a customer by key via the DAO. If the key is not found,
     * prints a user-friendly message and returns null.
     *
     * @param key the customer key to search for
     * @return the matching customer, or null if not found
     */
    public Customer searchCustomer(String key) {
        try {
            return customerDao.findByKey(key);
        } catch (RecordNotFoundException e) {
            System.out.println("Customer with key " + key + " not found");
            return null;
        }
    }
}
