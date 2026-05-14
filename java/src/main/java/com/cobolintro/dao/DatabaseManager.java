package com.cobolintro.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:./cobolintro;AUTO_SERVER=TRUE");
    }

    public static void initializeDatabase() throws SQLException {
        // Tables created by main DatabaseManager
    }
}
