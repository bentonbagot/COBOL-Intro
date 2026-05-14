package com.cobolintro.dao;

import com.cobolintro.exception.DuplicateKeyException;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the {@code products} table.
 * Replaces COBOL indexed file operations on PRODUCTS.DAT.
 */
public class ProductDao {

    /**
     * Inserts a new product record.
     *
     * @param p the product to insert
     * @throws DuplicateKeyException if a product with the same code already exists
     */
    public void create(Product p) {
        String sql = "INSERT INTO products (code, name, price, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCode());
            ps.setString(2, p.getName());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                throw new DuplicateKeyException(p.getCode());
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds a product by its code.
     *
     * @param code the product code to search for
     * @return the matching {@link Product}
     * @throws RecordNotFoundException if no product with the given code exists
     */
    public Product findByCode(String code) {
        String sql = "SELECT code, name, price, stock FROM products WHERE code = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RecordNotFoundException(code);
    }

    /**
     * Updates an existing product record by code.
     *
     * @param p the product with updated fields
     */
    public void update(Product p) {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ? WHERE code = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all products in the table.
     *
     * @return a list of all {@link Product} records
     */
    public List<Product> findAll() {
        String sql = "SELECT code, name, price, stock FROM products";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }
}
