package tn.esprit.market_3a33.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/market_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10); // Adjust pool size as needed
        config.setMinimumIdle(2); // Minimum idle connections
        config.setIdleTimeout(30000); // Idle timeout in milliseconds
        config.setMaxLifetime(1800000); // Maximum lifetime of a connection
        config.setConnectionTimeout(30000); // Connection timeout in milliseconds
        config.setLeakDetectionThreshold(5000); // Detect leaks after 5 seconds

        dataSource = new HikariDataSource(config);
    }

    private MyDatabase() {
        // Private constructor to prevent instantiation
    }

    public static Connection getCon() throws SQLException {
        return dataSource.getConnection(); // Get a connection from the pool
    }

    public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close(); // Return the connection to the pool
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}