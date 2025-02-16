package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices {
    private final Connection con;

    public UserServices() {
        con = MyDatabase.getInstance().getCon();
    }

    public void addUser(User user) throws SQLException {
        String query = "INSERT INTO users (cin, nom, prenom, email, password, role) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getCin());
            ps.setString(2, user.getNom());
            ps.setString(3, user.getPrenom());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole());

            ps.executeUpdate();
        }
    }

    public User authenticate(String email, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
            return null;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        }
        return users;
    }
}