package org.example.mdintech.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.mdintech.entities.User;
import org.example.mdintech.utils.PasswordVerification;
import org.example.mdintech.utils.UserRole;
import org.example.mdintech.service.userService;
import org.example.mdintech.utils.navigation;

import java.io.IOException;

public class SignInController {

    @FXML
    private TextField nameField, cinField, emailField, phoneField, addressField, cityField, stateField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Label passwordStrengthLabel;


    private final userService userService = new userService(); // Service for saving users

    @FXML
    private void handleSignIn(ActionEvent event) { // Add ActionEvent parameter
        try {
            String name = nameField.getText();
            int cin = Integer.parseInt(cinField.getText());
            String email = emailField.getText();
            String password = passwordField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String city = cityField.getText();
            String state = stateField.getText();
            UserRole role = UserRole.USER;

            if (!PasswordVerification.isStrongPassword(password)) {
                showAlert("Weak Password", "Password must be at least 8 characters long, contain at least one uppercase letter, " +
                        "one lowercase letter, one number, and one special character.");
                return;
            }

            User newUser = new User(name, cin, email, password, role, phone, address, city, state);
            userService.save(newUser);
            showAlert("Success", "User registered successfully!");

            navigation.switchScene(event, "/org/example/mdintech/userModule/login-view.fxml");

        } catch (NumberFormatException e) {
            showAlert("Error", "CIN must be a valid number!");
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void goToDashboard(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardRoot = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void checkPasswordStrength() {
        String password = passwordField.getText();
        if (PasswordVerification.isStrongPassword(password)) {
            passwordStrengthLabel.setText("Strong password.");
            passwordStrengthLabel.setStyle("-fx-text-fill: green;");
        } else {
            passwordStrengthLabel.setText("Weak password! Must have at least 8 characters, one uppercase, one lowercase, one number, and one special character.");
            passwordStrengthLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void handleBackButton(ActionEvent event) throws IOException {
        navigation.switchScene(event, "/org/example/mdintech/userModule/login-view.fxml");
    }
}
