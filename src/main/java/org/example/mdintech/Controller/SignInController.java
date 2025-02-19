package org.example.mdintech.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.mdintech.entities.User;
import org.example.mdintech.utils.PasswordVerification;
import org.example.mdintech.utils.UserRole;
import org.example.mdintech.service.userService;

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
    private void handleSignIn() {
        try {
            String name = nameField.getText();
            int cin = Integer.parseInt(cinField.getText());
            String email = emailField.getText();
            String password = passwordField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String city = cityField.getText();
            String state = stateField.getText();
            UserRole role= UserRole.USER;

            if (!PasswordVerification.isStrongPassword(password)) {
                showAlert("Weak Password", "Password must be at least 8 characters long, contain at least one uppercase letter, " +
                        "one lowercase letter, one number, and one special character.");
                return;
            }

            User newUser = new User(name, cin, email, password,role , phone, address, city, state);
            userService.save(newUser);
            showAlert("Success", "User registered successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "CIN must be a valid number!");
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
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

}
