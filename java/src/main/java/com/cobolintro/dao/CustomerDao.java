package com.cobolintro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cobolintro.exception.DuplicateKeyException;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.Customer;

/**
 * Data Access Object for Customer records.
 * Backed by H2 table: customers (key VARCHAR(10) PRIMARY KEY, name VARCHAR(30), phone VARCHAR(15)).
 * Replaces COBOL indexed file operations on clients.idx.
 */
public class CustomerDao {

    /**
     * Inserts a new customer into the customers table.
     *
     * @param customer the customer to add
     * @throws DuplicateKeyException if a customer with the same key already exists
     */
    public void add(Customer customer) {
        String sql = "INSERT INTO customers (key, name, phone) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getKey());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getPhone());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505 || (e.getSQLState() != null && e.getSQLState().startsWith("23"))) {
                throw new DuplicateKeyException(customer.getKey());
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds a customer by its primary key.
     *
     * @param key the customer key to search for
     * @return the matching customer
     * @throws RecordNotFoundException if no customer with the given key exists
     */
    public Customer findByKey(String key) {
        String sql = "SELECT key, name, phone FROM customers WHERE key = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getString("key"), rs.getString("name"), rs.getString("phone"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RecordNotFoundException(key);
    }

    /**
     * Returns all customers in the table.
     *
     * @return list of all customers
     */
    public List<Customer> findAll() {
        String sql = "SELECT key, name, phone FROM customers";
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(new Customer(rs.getString("key"), rs.getString("name"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }
}
