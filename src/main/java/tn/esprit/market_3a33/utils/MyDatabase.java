package tn.esprit.market_3a33.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/market_db";  // Remplace 'mdintech_db' par le vrai nom de ta base
    private static final String USER = "root";  // Mets ton utilisateur MySQL
    private static final String PASSWORD = "";  // Mets ton mot de passe MySQL

    private static MyDatabase instance;
    private Connection con;

    private MyDatabase() {
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Connexion établie avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur de connexion : " + e.getMessage());
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getCon() {
        return con;
    }
}
