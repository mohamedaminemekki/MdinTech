package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
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
    private void handleConfirmPayment() {
        // Récupérer les données du formulaire
        String cardNumber = cardNumberField.getText();
        String expirationDate = expirationDateField.getText();
        String securityCode = securityCodeField.getText();
        String cardHolderName = cardHolderNameField.getText();


        if (cardNumber.isEmpty() || expirationDate.isEmpty() || securityCode.isEmpty() || cardHolderName.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs du formulaire de paiement.");
            return;
        }


        boolean paymentSuccess = processPayment(cardNumber, expirationDate, securityCode, cardHolderName);

        if (paymentSuccess) {

            reservation.setPaymentStatus("Paid");
            reservation.setStatus("Confirmed");

            try {

                reservationService.add(reservation);


                showAlert("Paiement réussi", "Votre paiement a été effectué avec succès. Votre réservation est confirmée.");


                Stage stage = (Stage) confirmPaymentButton.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'ajout de la réservation à la base de données.");
            }
        } else {
            showAlert("Paiement échoué", "Le paiement n'a pas pu être effectué. Veuillez réessayer.");
        }
    }

    private boolean processPayment(String cardNumber, String expirationDate, String securityCode, String cardHolderName) {

        System.out.println("Traitement du paiement...");
        System.out.println("Numéro de carte : " + cardNumber);
        System.out.println("Date d'expiration : " + expirationDate);
        System.out.println("Code de sécurité : " + securityCode);
        System.out.println("Nom du titulaire : " + cardHolderName);

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}