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

public class EditTripController {

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

    private Trip trip;
    private TripService tripService;

    public void setTrip(Trip trip) {
        this.trip = trip;
        // Remplir les champs avec les données du trajet
        transportIdField.setText(String.valueOf(trip.getTransportId()));
        departureTimeField.setText(trip.getDepartureTime().toString());
        arrivalTimeField.setText(trip.getArrivalTime().toString());
        priceField.setText(String.valueOf(trip.getPrice()));
        departureField.setText(trip.getDeparture());
        destinationField.setText(trip.getDestination());
        transportNameField.setText(trip.getTransportName());
    }

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

        // Validation pour Departure Time et Arrival Time (doit être une date valide)
        departureTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isValidTimestamp(newValue)) {
                showAlert("Erreur de saisie", "Le format de la date de départ est invalide (yyyy-MM-dd HH:mm:ss).");
            }
        });

        arrivalTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isValidTimestamp(newValue)) {
                showAlert("Erreur de saisie", "Le format de la date d'arrivée est invalide (yyyy-MM-dd HH:mm:ss).");
            }
        });

        // Validation pour Price (doit être un nombre décimal)
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(newValue.replaceAll("[^\\d.]", ""));
                showAlert("Erreur de saisie", "Le prix doit être un nombre décimal.");
            }
        });
    }

    @FXML
    private void saveTrip() {
        // Vérifier si tous les champs obligatoires sont remplis
        if (transportIdField.getText().isEmpty() || departureTimeField.getText().isEmpty() ||
                arrivalTimeField.getText().isEmpty() || priceField.getText().isEmpty() ||
                departureField.getText().isEmpty() || destinationField.getText().isEmpty() ||
                transportNameField.getText().isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        try {
            // Mettre à jour les données du trajet
            trip.setTransportId(Integer.parseInt(transportIdField.getText()));
            trip.setDepartureTime(Timestamp.valueOf(departureTimeField.getText()));
            trip.setArrivalTime(Timestamp.valueOf(arrivalTimeField.getText()));
            trip.setPrice(Double.parseDouble(priceField.getText()));
            trip.setDeparture(departureField.getText());
            trip.setDestination(destinationField.getText());
            trip.setTransportName(transportNameField.getText());

            // Enregistrer les modifications dans la base de données
            tripService.update(trip);

            // Fermer la fenêtre
            transportIdField.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de mettre à jour le trajet.");
        }
    }

    @FXML
    private void returnToPreviousPage() {
        // Fermer la fenêtre actuelle
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