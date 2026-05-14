package com.cobolintro.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class that manages the H2 database connection and schema initialization.
 * Replaces COBOL flat-file storage with an embedded relational database.
 */
public class DatabaseManager {

    private static final String URL = "jdbc:h2:./cobolintro;AUTO_SERVER=TRUE";

    /**
     * Returns a JDBC connection to the embedded H2 database.
     *
     * @return a new {@link Connection} instance
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Creates all required tables if they do not already exist.
     * Safe to call multiple times.
     *
     * @throws SQLException if any DDL statement fails
     */
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS products ("
                    + "code VARCHAR(5) PRIMARY KEY, "
                    + "name VARCHAR(20), "
                    + "price DECIMAL(9,2), "
                    + "stock INT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS sales ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "product_code VARCHAR(5), "
                    + "quantity INT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS customers ("
                    + "key VARCHAR(10) PRIMARY KEY, "
                    + "name VARCHAR(30), "
                    + "phone VARCHAR(15))");
        }
    }
}
