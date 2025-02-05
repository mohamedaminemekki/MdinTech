package org.example.mdintech.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
    private static dbConnection instance;
    private Connection connection;
    private String dbPort="3306";
    private String host="localhost";
    private String dbName="mdintech";
    private final String url = "jdbc:mysql://"+host+":"+dbPort+"/"+dbName;
    private final String user = "root";
    private final String password = "";

    // Private constructor to prevent external instantiation
    private dbConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get the single instance
    public static dbConnection getInstance() {
        if (instance == null) {
            synchronized (dbConnection.class) { // Thread-safe singleton
                if (instance == null) {
                    instance = new dbConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConn() {
        return connection;
    }
}
