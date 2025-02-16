package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UserServices userService = new UserServices();

    @FXML
    private void handleLogin() {
        try {
            User user = userService.authenticate(
                    emailField.getText(),
                    passwordField.getText()
            );

            if (user != null) {
                SessionManager.setCurrentUser(user);

                // Redirection basée sur le rôle
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    loadAdminInterface();
                } else {
                    loadUserInterface(user);
                }
            } else {
                showAlert("Erreur", "Identifiants invalides");
            }
        } catch (SQLException e) {
            showAlert("Erreur BD", e.getMessage());
        }
    }

    private void loadAdminInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/Facture.fxml"));
            Parent root = loader.load();

            FactureController controller = loader.getController();
            controller.initializeAdminData();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin - Gestion des Factures");
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'interface admin");
        }
    }

    private void loadUserInterface(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/UserInterface.fxml"));
            Parent root = loader.load();

            UserController userController = loader.getController();
            userController.setCurrentUser(user);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Espace Utilisateur");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'interface utilisateur.");
        }
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}