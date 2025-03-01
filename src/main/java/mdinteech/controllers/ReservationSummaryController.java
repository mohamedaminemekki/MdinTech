package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ReservationSummaryController {

    @FXML
    private Label reservationDetailsLabel;

    private Reservation reservation;
    private ReservationService reservationService;

    private String selectedDeparture;
    private String selectedDestination;
    private String selectedTransportName;
    private double selectedTripPrice;

    public ReservationSummaryController() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            reservationService = new ReservationService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReservationDetails(Reservation reservation, String departure, String destination, String transportName, double totalPrice) {
        this.reservation = reservation;
        this.selectedDeparture = departure;
        this.selectedDestination = destination;
        this.selectedTransportName = transportName;
        this.selectedTripPrice = totalPrice;
        displayReservationDetails(departure, destination, transportName, totalPrice);
    }

    private void displayReservationDetails(String departure, String destination, String transportName, double totalPrice) {
        String details = "Détails de la réservation :\n" +
                "Trajet : " + departure + " → " + destination + "\n" +
                "Transport : " + transportName + "\n" +
                "Nombre de passagers : " + reservation.getSeatNumber() + "\n" +
                "Type de siège : " + reservation.getSeatType() + "\n" +
                "Prix total : " + totalPrice + " DT\n" +
                "Statut : " + reservation.getStatus();
        reservationDetailsLabel.setText(details);
    }

    @FXML
    private void handleModify() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/ReservationDetails.fxml"));
            Parent root = loader.load();

            ReservationDetailsController controller = loader.getController();

            Trip currentTrip = new Trip(
                    reservation.getTripId(),
                    reservation.getTransportId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    selectedTripPrice,
                    selectedDeparture,
                    selectedDestination,
                    selectedTransportName
            );

            controller.setTripDetails(currentTrip);
            controller.setPassengerSpinnerValue(reservation.getSeatNumber());
            controller.setSeatTypeComboBoxValue(reservation.getSeatType());

            Stage stage = new Stage();
            stage.setTitle("Modifier la Réservation");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) reservationDetailsLabel.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirm() {
        try {
            if (reservation.getPaymentStatus().equals("Pending")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/PaymentPage.fxml"));
                Parent root = loader.load();

                PaymentController paymentController = loader.getController();
                paymentController.setReservation(reservation);
                paymentController.setTotalPrice(selectedTripPrice);

                Stage stage = new Stage();
                stage.setTitle("Paiement");
                stage.setScene(new Scene(root));
                stage.show();

                Stage currentStage = (Stage) reservationDetailsLabel.getScene().getWindow();
                currentStage.close();
            } else {
                if (!reservationService.isReservationExists(reservation.getTripId(), reservation.getUserId())) {
                    reservation.setStatus("Pending"); // Statut mis à "Pending" si la réservation est créée mais non payée
                    reservationService.add(reservation);
                    showConfirmationMessage();
                } else {
                    showAlert("Erreur", "Cette réservation existe déjà.");
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur est survenue lors de la réservation.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void showConfirmationMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation de Réservation");
        alert.setHeaderText("Réservation confirmée");
        alert.setContentText("Votre réservation est valable pour 24 heures. Passé ce délai, elle sera annulée si le paiement n'est pas effectué.");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}