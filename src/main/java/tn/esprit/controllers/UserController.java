package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;

import java.sql.SQLException;

public class UserController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;

    private final UserService userService = new UserService();

    @FXML
    private void handleAddUser() {
        try {
            User newUser = new User(
                    usernameField.getText(),
                    emailField.getText(),
                    passwordField.getText()
            );

            userService.add(newUser);
            showAlert("Succès", "Utilisateur ajouté avec succès !");
            clearFields();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
        }
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
