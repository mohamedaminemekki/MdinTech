package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ModifyReservationController {

    @FXML
    private Label departureLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private TextField passengersField;

    @FXML
    private ComboBox<String> seatTypeComboBox;

    private Reservation reservation;
    private Trip trip;
    private ReservationService reservationService;
    private TripService tripService;

    public ModifyReservationController() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", "");
            reservationService = new ReservationService(connection);
            tripService = new TripService(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        loadReservationDetails();
    }

    private void loadReservationDetails() {
        try {
            // Récupérer les détails du trajet associé à la réservation en utilisant tripId
            trip = tripService.getById(reservation.getTripId());

            if (trip != null) {
                // Afficher les informations du trajet (départ, destination, prix)
                departureLabel.setText(trip.getDeparture());
                destinationLabel.setText(trip.getDestination());
                priceLabel.setText(String.valueOf(trip.getPrice()));

                // Afficher les valeurs actuelles de la réservation (nombre de passagers, type de siège)
                passengersField.setText(String.valueOf(reservation.getSeatNumber()));
                seatTypeComboBox.getItems().addAll("Standard", "Premium");
                seatTypeComboBox.setValue(reservation.getSeatType());
            } else {
                showAlert("Erreur", "Aucun trajet trouvé pour cette réservation.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les détails de la réservation.");
        }
    }

    @FXML
    private void handleSaveChanges() {
        try {
            // Valider les champs avant de sauvegarder
            if (passengersField.getText().isEmpty() || seatTypeComboBox.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            // Valider que le nombre de passagers est un entier positif
            int passengers;
            try {
                passengers = Integer.parseInt(passengersField.getText());
                if (passengers <= 0) {
                    showAlert("Erreur", "Le nombre de passagers doit être un entier positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le nombre de passagers doit être un nombre valide.");
                return;
            }

            // Mettre à jour les détails de la réservation
            reservation.setSeatNumber(passengers);
            reservation.setSeatType(seatTypeComboBox.getValue());

            // Enregistrer les modifications dans la base de données
            reservationService.update(reservation);

            showAlert("Succès", "Les modifications ont été enregistrées avec succès.");

            // Fermer la fenêtre de modification
            Stage stage = (Stage) passengersField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement des modifications.");
        }
    }

    @FXML
    private void handleBack() {
        // Fermer la fenêtre actuelle et revenir à la liste des réservations
        Stage stage = (Stage) passengersField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}