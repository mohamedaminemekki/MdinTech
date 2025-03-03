package tn.esprit.market_3a33.services;

import tn.esprit.market_3a33.entities.User;
import tn.esprit.market_3a33.utils.MyDatabase;
import tn.esprit.market_3a33.entities.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    // Method to get user role by CIN
    public UserRole getUserRole(int CIN) {
        String query = "SELECT Role FROM users WHERE CIN = ?"; // Adjust table and column names as needed
        UserRole role = UserRole.Citizen; // Default role if user not found

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get the database connection instance
            con = MyDatabase.getCon();
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, CIN); // Set the CIN parameter
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Fetch the role from the database and convert it to UserRole enum
                String roleStr = rs.getString("Role");
                try {
                    role = UserRole.valueOf(roleStr); // Convert the role string to UserRole enum
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid role value in database: " + roleStr);
                    // Default role (Citizen) will be returned
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user role: " + e.getMessage());
        } finally {
            // Close resources in the reverse order of their creation
            MyDatabase.close(con, pstmt, rs);
        }

        return role;
    }
}