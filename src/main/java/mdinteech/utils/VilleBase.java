package mdinteech.utils;

import mdinteech.entities.Ville;
import java.sql.*;

public class VilleBase {
    private static final String URL = "jdbc:mysql://localhost:3306/city_transport";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Ville getVille(String nom) {
        String sql = "SELECT * FROM villes WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                return new Ville(
                        rs.getString("nom"),
                        rs.getString("histoire"),
                        rs.getString("anecdotes"),
                        rs.getString("activites"),
                        rs.getString("gastronomie"),
                        rs.getString("nature"),
                        rs.getString("histoire_interactive")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}