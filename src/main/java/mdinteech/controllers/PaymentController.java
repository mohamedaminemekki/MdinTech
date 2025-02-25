package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.services.PaymeeService;
import mdinteech.services.PaymentService;
import mdinteech.services.ReservationService;
import mdinteech.utils.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class PaymentController {

    @FXML
    private Label paymentDetailsLabel;

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField expirationDateField;

    @FXML
    private TextField securityCodeField;

    @FXML
    private TextField cardHolderNameField;

    @FXML
    private Button confirmPaymentButton;

    private Reservation reservation;
    private ReservationService reservationService;
    private double totalPrice;

    public PaymentController() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            reservationService = new ReservationService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        displayPaymentDetails();
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        displayPaymentDetails();
    }

    @FXML
    private void displayPaymentDetails() {
        if (paymentDetailsLabel != null) {
            String details = "Détails du paiement :\n" +
                    "Trajet : " + reservation.getTripId() + "\n" +
                    "Nombre de passagers : " + reservation.getSeatNumber() + "\n" +
                    "Type de siège : " + reservation.getSeatType() + "\n" +
                    "Montant à payer : " + totalPrice + " DT";
            paymentDetailsLabel.setText(details);
        } else {
            System.err.println("Erreur : paymentDetailsLabel est null.");
        }
    }

    @FXML
    private void handleConfirmPayment() throws IOException {
        String orderReference = "CMD-" + reservation.getId();
        String customerName = cardHolderNameField.getText();
        double amount = totalPrice;

        String paymentUrl = PaymeeService.createTransaction(amount, orderReference, customerName, "https://monapp.com/success");

        if (paymentUrl != null) {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(paymentUrl));

            // Attendre 10 secondes avant de vérifier le paiement
            try {
                Thread.sleep(10000);
                String status = PaymeeService.checkPaymentStatus(orderReference);
                if ("paid".equals(status)) {
                    showAlert("Paiement réussi", "Votre paiement a été confirmé !");
                } else {
                    showAlert("En attente", "Le paiement est en cours de validation.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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