package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String url = "jdbc:mysql://localhost:3306/esprit";
    private final String user = "root";
    private final String password = "";
    private Connection conn;
    private static MyDataBase instance;

    private MyDataBase() {
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            synchronized (MyDataBase.class) {
                if (instance == null) {
                    instance = new MyDataBase();
                }
            }
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}
