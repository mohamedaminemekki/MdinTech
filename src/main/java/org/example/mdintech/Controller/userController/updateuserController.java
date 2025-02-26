package org.example.mdintech.Controller.userController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.mdintech.Singleton.loggedInUser;
import org.example.mdintech.entities.User;
import org.example.mdintech.service.userService;
import org.example.mdintech.utils.PasswordVerification;
import org.example.mdintech.utils.navigation;

import java.io.IOException;


public class updateuserController {
    @FXML
    private TextField nameField, emailField, phoneField, addressField, cityField, stateField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button updateButton;

    private userService userService = new userService();
    private User currentUser;
    private User originalUser;  // Store original values for comparison

    @FXML
    public void initialize() {
        currentUser = loggedInUser.getInstance().getLoggedUser();
        if (currentUser != null) {
            // Create a copy of the original user data
            originalUser = new User(
                    currentUser.getName(),
                    currentUser.getCIN(),
                    currentUser.getEmail(),
                    currentUser.getPassword(),
                    currentUser.getRole(),  // Include role
                    currentUser.getPhone(),
                    currentUser.getAddress(),
                    currentUser.getCity(),
                    currentUser.getState(),
                    currentUser.getPathtopic(),
                    currentUser.getBirthday()// Include status if using second constructor
            );

            // Initialize fields with current values
            nameField.setText(originalUser.getName());
            emailField.setText(originalUser.getEmail());
            phoneField.setText(originalUser.getPhone());
            addressField.setText(originalUser.getAddress());
            cityField.setText(originalUser.getCity());
            stateField.setText(originalUser.getState());
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (currentUser == null) {
            statusLabel.setText("No user is logged in.");
            return;
        }

        // Get field values with trim()
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newAddress = addressField.getText().trim();
        String newCity = cityField.getText().trim();
        String newState = stateField.getText().trim();
        String newPassword = passwordField.getText().trim(); // New password input

        boolean changesDetected = false;

        // Check each field for changes
        if (!newName.equals(originalUser.getName())) {
            currentUser.setName(newName);
            changesDetected = true;
        }

        if (!newEmail.equals(originalUser.getEmail())) {
            currentUser.setEmail(newEmail);
            changesDetected = true;
        }

        if (!newPhone.equals(originalUser.getPhone())) {
            currentUser.setPhone(newPhone);
            changesDetected = true;
        }

        if (!newAddress.equals(originalUser.getAddress())) {
            currentUser.setAddress(newAddress);
            changesDetected = true;
        }

        if (!newCity.equals(originalUser.getCity())) {
            currentUser.setCity(newCity);
            changesDetected = true;
        }

        if (!newState.equals(originalUser.getState())) {
            currentUser.setState(newState);
            changesDetected = true;
        }

        boolean passwordChanged = false;
        if (!newPassword.isEmpty()) {
            if (!PasswordVerification.verifyPassword(newPassword, originalUser.getPassword())) {
                passwordChanged = true;
                changesDetected = true;
            } else {
                passwordField.clear(); // Clear field if password is unchanged
            }
        }

        if (!changesDetected) {
            statusLabel.setText("No changes detected.");
            return;
        }

        try {
            if (passwordChanged) {
                userService.update(currentUser);
            } else {
                userService.updateUserWithoutPassword(currentUser);
            }

            originalUser = new User(
                    currentUser.getName(),
                    currentUser.getCIN(),
                    currentUser.getEmail(),
                    originalUser.getPassword(), // Use original password if not changed
                    currentUser.getRole(),
                    currentUser.getPhone(),
                    currentUser.getAddress(),
                    currentUser.getCity(),
                    currentUser.getState(),
                    currentUser.getPathtopic(),
                    currentUser.getBirthday()
            );

            statusLabel.setText("User updated successfully!");
            passwordField.clear();
            navigation.switchScene(event, "/org/example/mdintech/main-user-view.fxml");
        } catch (Exception e) {
            statusLabel.setText("Update failed: " + e.getMessage());
        }
    }

    public void handleBackButton(ActionEvent event) throws IOException {
        navigation.switchScene(event, "/org/example/mdintech/main-user-view.fxml");
    }
}
