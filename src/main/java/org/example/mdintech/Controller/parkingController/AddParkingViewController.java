package org.example.mdintech.Controller.parkingController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.service.ParkingModule.ParkingService;

public class AddParkingViewController {

    @FXML
    private TextField nameField;  // Ensure this matches the FXML

    @FXML
    private TextField locationField;  // Ensure this matches the FXML

    @FXML
    private TextField capacityField;  // Ensure this matches the FXML

    @FXML
    private Button addParkingButton;

    private final ParkingService parkingService = new ParkingService();

    @FXML
    private void handleAddParking(ActionEvent event) {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String capacityText = capacityField.getText().trim();

        // Validate input fields
        if (name.isEmpty() || location.isEmpty() || capacityText.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                showAlert("Validation Error", "Capacity must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Capacity must be a valid number.");
            return;
        }

        // Create a new Parking object
        Parking newParking = new Parking(name, location, capacity);

        // Save it using the service
        boolean success = parkingService.save(newParking);

        if (success) {
            showAlert("Success", "Parking added successfully!");
            clearFields();
        } else {
            showAlert("Error", "Failed to add parking. Please try again.");
        }
    }

    private void clearFields() {
        nameField.clear();
        locationField.clear();
        capacityField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
