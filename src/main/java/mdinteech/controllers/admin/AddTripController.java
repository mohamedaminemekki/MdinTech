package mdinteech.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import mdinteech.entities.Trip;
import mdinteech.services.TripService;

import java.sql.Timestamp;

public class AddTripController {

    @FXML
    private TextField transportIdField;
    @FXML
    private TextField departureTimeField;
    @FXML
    private TextField arrivalTimeField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField departureField;
    @FXML
    private TextField destinationField;
    @FXML
    private TextField transportNameField;
    @FXML
    private Button returnButton;

    private TripService tripService;

    public void setTripService(TripService tripService) {
        this.tripService = tripService;
    }

    @FXML
    private void initialize() {
        // Validation pour Transport ID (doit être un entier)
        transportIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                transportIdField.setText(newValue.replaceAll("[^\\d]", ""));
                showAlert("Erreur de saisie", "Le Transport ID doit être un nombre entier.");
            }
        });


        departureTimeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Lorsque le champ perd le focus
                if (!isValidTimestamp(departureTimeField.getText())) {
                    showAlert("Erreur de saisie", "Le format de la date de départ est invalide (yyyy-MM-dd HH:mm:ss).");
                }
            }
        });

        arrivalTimeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!isValidTimestamp(arrivalTimeField.getText())) {
                    showAlert("Erreur de saisie", "Le format de la date d'arrivée est invalide (yyyy-MM-dd HH:mm:ss).");
                }
            }
        });


        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(newValue.replaceAll("[^\\d.]", ""));
                showAlert("Erreur de saisie", "Le prix doit être un nombre décimal.");
            }
        });
    }

    @FXML
    private void saveTrip() {

        if (transportIdField.getText().isEmpty() || departureTimeField.getText().isEmpty() ||
                arrivalTimeField.getText().isEmpty() || priceField.getText().isEmpty() ||
                departureField.getText().isEmpty() || destinationField.getText().isEmpty() ||
                transportNameField.getText().isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        try {

            Trip trip = new Trip(
                    0, // L'ID sera généré par la base de données
                    Integer.parseInt(transportIdField.getText()),
                    Timestamp.valueOf(departureTimeField.getText()),
                    Timestamp.valueOf(arrivalTimeField.getText()),
                    Double.parseDouble(priceField.getText()),
                    departureField.getText(),
                    destinationField.getText(),
                    transportNameField.getText()
            );


            tripService.add(trip);


            transportIdField.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le trajet.");
        }
    }

    @FXML
    private void returnToPreviousPage() {

        transportIdField.getScene().getWindow().hide();
    }

    private boolean isValidTimestamp(String timestamp) {
        try {
            Timestamp.valueOf(timestamp);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}