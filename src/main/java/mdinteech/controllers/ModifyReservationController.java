package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

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

    private double initialPrice;
    private double newPrice;

    public ModifyReservationController() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            tripService = new TripService(connection);
            reservationService = new ReservationService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        loadReservationDetails();
    }

    private void loadReservationDetails() {
        try {

            trip = tripService.getById(reservation.getTripId());

            if (trip != null) {

                departureLabel.setText(trip.getDeparture());
                destinationLabel.setText(trip.getDestination());
                priceLabel.setText(String.valueOf(trip.getPrice()));


                initialPrice = calculateTotalPrice(reservation.getSeatNumber(), reservation.getSeatType());


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

            if (passengersField.getText().isEmpty() || seatTypeComboBox.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs.");
                return;
            }

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

            newPrice = calculateTotalPrice(passengers, seatTypeComboBox.getValue());


            double priceDifference = newPrice - initialPrice;
            if (priceDifference > 0) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Différence de prix");
                alert.setHeaderText("Il y a une différence de prix à payer.");
                alert.setContentText("Montant à payer : " + priceDifference + " DT. Voulez-vous continuer ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {

                    redirectToPayment(priceDifference);
                }
            } else {

                reservation.setSeatNumber(passengers);
                reservation.setSeatType(seatTypeComboBox.getValue());


                reservationService.update(reservation);

                showAlert("Succès", "Les modifications ont été enregistrées avec succès.");


                Stage stage = (Stage) passengersField.getScene().getWindow();
                stage.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement des modifications.");
        }
    }

    private double calculateTotalPrice(int passengers, String seatType) {
        double basePrice = trip.getPrice();
        double premiumFee = seatType.equals("Premium") ? 10.0 : 0.0;
        return (basePrice + premiumFee) * passengers;
    }

    private void redirectToPayment(double priceDifference) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/PaymentPage.fxml"));
            Parent root = loader.load();


            PaymentController paymentController = loader.getController();
            paymentController.setReservation(reservation);
            paymentController.setTotalPrice(priceDifference);


            Stage stage = new Stage();
            stage.setTitle("Paiement");
            stage.setScene(new Scene(root));
            stage.show();


            Stage currentStage = (Stage) passengersField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de paiement.");
        }
    }

    @FXML
    private void handleBack() {

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