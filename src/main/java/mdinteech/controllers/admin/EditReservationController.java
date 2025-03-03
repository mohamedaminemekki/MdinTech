package mdinteech.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;

public class EditReservationController {

    @FXML
    private TextField userIdField;
    @FXML
    private TextField tripIdField;
    @FXML
    private TextField transportIdField;
    @FXML
    private TextField seatNumberField;
    @FXML
    private TextField seatTypeField;
    @FXML
    private TextField paymentStatusField;
    @FXML
    private Button returnButton;

    private Reservation reservation;
    private ReservationService reservationService;

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;

        userIdField.setText(String.valueOf(reservation.getUserId()));
        tripIdField.setText(String.valueOf(reservation.getTripId()));
        transportIdField.setText(String.valueOf(reservation.getTransportId()));
        seatNumberField.setText(String.valueOf(reservation.getSeatNumber()));
        seatTypeField.setText(reservation.getSeatType());
        paymentStatusField.setText(reservation.getPaymentStatus());
    }

    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @FXML
    private void initialize() {

        userIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                userIdField.setText(newValue.replaceAll("[^\\d]", ""));
                showAlert("Erreur de saisie", "L'ID utilisateur doit être un nombre entier.");
            }
        });

        tripIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tripIdField.setText(newValue.replaceAll("[^\\d]", ""));
                showAlert("Erreur de saisie", "L'ID du trajet doit être un nombre entier.");
            }
        });

        transportIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                transportIdField.setText(newValue.replaceAll("[^\\d]", ""));
                showAlert("Erreur de saisie", "L'ID du transport doit être un nombre entier.");
            }
        });

        seatNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                seatNumberField.setText(newValue.replaceAll("[^\\d]", ""));
                showAlert("Erreur de saisie", "Le numéro de siège doit être un nombre entier.");
            }
        });
    }

    @FXML
    private void saveReservation() {

        if (userIdField.getText().isEmpty() || tripIdField.getText().isEmpty() ||
                transportIdField.getText().isEmpty() || seatNumberField.getText().isEmpty() ||
                seatTypeField.getText().isEmpty() || paymentStatusField.getText().isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        try {

            reservation.setUserId(Integer.parseInt(userIdField.getText()));
            reservation.setTripId(Integer.parseInt(tripIdField.getText()));
            reservation.setTransportId(Integer.parseInt(transportIdField.getText()));
            reservation.setSeatNumber(Integer.parseInt(seatNumberField.getText()));
            reservation.setSeatType(seatTypeField.getText());
            reservation.setPaymentStatus(paymentStatusField.getText());


            reservationService.update(reservation);


            userIdField.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de mettre à jour la réservation.");
        }
    }

    @FXML
    private void returnToPreviousPage() {

        userIdField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}