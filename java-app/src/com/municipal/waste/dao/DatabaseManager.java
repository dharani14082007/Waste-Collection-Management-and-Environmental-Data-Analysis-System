package com.municipal.waste.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connectivity with support for SQLite JDBC and graceful fallback.
 * Author: DHARANI K K (Register No.: 23)
 */
public class DatabaseManager {

    private static final String DEFAULT_SQLITE_PATH = "database" + File.separator + "waste_management.db";
    private static Connection connection = null;
    private static boolean driverAvailable = true;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // Driver not present in runtime classpath; DAO falls back seamlessly to memory-store
            driverAvailable = false;
        }
    }

    public static boolean isDriverAvailable() {
        return driverAvailable;
    }

    public static Connection getConnection() {
        if (!driverAvailable) {
            return null;
        }
        try {
            if (connection == null || connection.isClosed()) {
                // Check relative paths
                File dbFile = new File(DEFAULT_SQLITE_PATH);
                if (!dbFile.exists()) {
                    dbFile = new File(".." + File.separator + DEFAULT_SQLITE_PATH);
                }
                String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
                connection = DriverManager.getConnection(url);
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Connection notice: " + e.getMessage());
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
