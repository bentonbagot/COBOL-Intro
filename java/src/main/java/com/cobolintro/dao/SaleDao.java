package com.cobolintro.dao;

import com.cobolintro.model.Sale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the {@code sales} table.
 * Replaces COBOL sequential file operations on SELLS.DAT.
 */
public class SaleDao {

    /**
     * Inserts a new sale record.
     *
     * @param s the sale to write
     */
    public void write(Sale s) {
        String sql = "INSERT INTO sales (product_code, quantity) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getProductCode());
            ps.setInt(2, s.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all sales records.
     *
     * @return a list of all {@link Sale} records
     */
    public List<Sale> readAll() {
        String sql = "SELECT product_code, quantity FROM sales";
        List<Sale> sales = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sales.add(new Sale(
                        rs.getString("product_code"),
                        rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }

    /**
     * Returns the total number of sale records.
     *
     * @return the count of rows in the sales table
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
