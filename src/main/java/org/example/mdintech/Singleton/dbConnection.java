package org.example.mdintech.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
    private static dbConnection instance;
    private Connection connection;
    private final String dbPort = "3306";
    private final String host = "localhost";
    private final String dbName = "mdintech";
    private final String url = "jdbc:mysql://" + host + ":" + dbPort + "/" + dbName +
            "?autoReconnect=true&useSSL=false"; // Added auto-reconnect
    private final String user = "root";
    private final String password = "";

    private dbConnection() {} // Private constructor

    public static synchronized dbConnection getInstance() {
        if (instance == null) {
            instance = new dbConnection();
        }
        return instance;
    }

    // Get a valid connection (reconnects if closed)
    public Connection getConn() {
        try {
            // Reconnect if the connection is closed or invalid
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                reconnect();
            }
        } catch (SQLException e) {
            System.err.println("Connection validation failed: " + e.getMessage());
            reconnect();
        }
        return connection;
    }

    // Reconnect explicitly
    private void reconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Reconnected to database!");
        } catch (SQLException e) {
            System.err.println("Reconnection failed: " + e.getMessage());
        }
    }
}