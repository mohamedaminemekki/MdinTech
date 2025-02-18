package mdinteech.services;

import mdinteech.entities.Trip;
import mdinteech.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements Services<User> {

    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
    }

    // Lire la liste des utilisateurs
    @Override
    public List<User> readList() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
                users.add(user);
            }
        }
        return users;
    }

    // Ajouter un utilisateur
    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());

            int rowsAffected = statement.executeUpdate();

            // Si l'insertion est réussie, récupérer l'ID généré automatiquement
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));  // Récupérer l'ID généré
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour un utilisateur
    @Override
    public void update(User user) throws SQLException {  // Ajout de throws SQLException
        String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            } else {
                System.out.println("Utilisateur mis à jour avec succès.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer un utilisateur
    @Override
    public void delete(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilisateur supprimé avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Trip getById(int id) throws SQLException {
        return null;
    }

    // Méthode pour ajouter un utilisateur spécifique
    @Override
    public void addP(User user) throws SQLException {
        add(user);  // Implémentation réutilisée de la méthode add
    }
}
