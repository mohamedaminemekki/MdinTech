package org.example.mdintech.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.example.mdintech.entities.User;
import org.example.mdintech.utils.UserRole;
import org.example.mdintech.service.userService;

import java.util.Date;

public class testingpageController {

    @FXML private Button createAdminButton;
    private userService userService;

    public testingpageController() {
        userService = new userService();
    }

    @FXML
    private void handleCreateAdmin() {
        Date birthday=new Date(System.currentTimeMillis());
        // Creating an admin user
        User adminUser = new User(
                "Admin User", 12345678, "admin@gmail.com", "Snprb120401#",
                UserRole.ADMIN, "123456789", "123 Admin Street", "Admin City", "Admin State","",birthday
        );

        // Saving the admin user to the database
        try {
            boolean success = userService.save(adminUser);
            if (success) {
                showAlert("Success", "Admin user created successfully!");
            } else {
                showAlert("Error", "Failed to create admin user.");
            }
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
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
