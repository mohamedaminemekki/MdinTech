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

    @FXML
    public void initialize() {
        currentUser = loggedInUser.getInstance().getLoggedUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            addressField.setText(currentUser.getAddress());
            cityField.setText(currentUser.getCity());
            stateField.setText(currentUser.getState());
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (currentUser == null) {
            statusLabel.setText("No user is logged in.");
            return;
        }

        // Get updated values
        currentUser.setName(nameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setPhone(phoneField.getText());
        currentUser.setAddress(addressField.getText());
        currentUser.setCity(cityField.getText());
        currentUser.setState(stateField.getText());

        // Check if the password was changed
        String newPassword = passwordField.getText();
        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        // Update in database
        userService.update(currentUser);
        statusLabel.setText("User updated successfully!");
    }
}
