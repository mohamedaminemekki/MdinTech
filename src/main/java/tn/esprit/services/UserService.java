package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    Connection con;

    public UserService() {
        con = MyDatabase.getInstance().getCon();
    }

    public void add(User user) throws SQLException {
        String query = "INSERT INTO `user`(`username`, `email`, `password`) VALUES (?,?,?)";
        PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            user.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Failed to retrieve the generated ID for user.");
        }
    }

    public List<User> readList() throws SQLException {
        String query = "SELECT * FROM `user`";
        List<User> users = new ArrayList<>();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(query);
        while (rs.next()) {
            User u = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password")
            );
            users.add(u);
        }
        return users;
    }

    public void update(User user) throws SQLException {
        String query = "UPDATE `user` SET `username` = ?, `email` = ?, `password` = ? WHERE `id` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setInt(4, user.getId());

        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("User updated successfully!");
        } else {
            System.out.println("No user found with ID: " + user.getId());
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM `user` WHERE `id` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsDeleted = ps.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("User deleted successfully!");
        } else {
            System.out.println("No user found with ID: " + id);
        }
    }
}
