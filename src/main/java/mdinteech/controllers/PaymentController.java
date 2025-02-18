package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;

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
    private Button confirmPaymentButton; // Doit correspondre au fx:id dans le FXML

    private Reservation reservation;
    private ReservationService reservationService;

    public PaymentController() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", "");
            reservationService = new ReservationService(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        displayPaymentDetails();
    }

    private void displayPaymentDetails() {
        if (paymentDetailsLabel != null) {
            String details = "Détails du paiement :\n" +
                    "Trajet : " + reservation.getTripId() + "\n" +
                    "Nombre de passagers : " + reservation.getSeatNumber() + "\n" +
                    "Type de siège : " + reservation.getSeatType() + "\n" +
                    "Montant à payer : " + calculateTotalPrice() + " DT";
            paymentDetailsLabel.setText(details);
        } else {
            System.err.println("Erreur : paymentDetailsLabel est null.");
        }
    }

    private double calculateTotalPrice() {
        // Calculer le prix total en fonction du nombre de passagers et du type de siège
        double basePrice = 50.0; // Exemple de prix de base
        double premiumFee = reservation.getSeatType().equals("Premium") ? 10.0 : 0.0;
        return (basePrice + premiumFee) * reservation.getSeatNumber();
    }

    @FXML
    private void handleConfirmPayment() {
        // Récupérer les données du formulaire
        String cardNumber = cardNumberField.getText();
        String expirationDate = expirationDateField.getText();
        String securityCode = securityCodeField.getText();
        String cardHolderName = cardHolderNameField.getText();

        // Valider les données
        if (cardNumber.isEmpty() || expirationDate.isEmpty() || securityCode.isEmpty() || cardHolderName.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs du formulaire de paiement.");
            return;
        }

        // Simuler un paiement réussi
        boolean paymentSuccess = processPayment(cardNumber, expirationDate, securityCode, cardHolderName);

        if (paymentSuccess) {
            // Mettre à jour le statut de paiement de la réservation
            reservation.setPaymentStatus("Paid");

            try {
                // Ajouter la réservation à la base de données
                reservationService.add(reservation);

                // Afficher un message de confirmation
                showAlert("Paiement réussi", "Votre paiement a été effectué avec succès. Votre réservation est confirmée.");

                // Fermer la fenêtre de paiement
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
        // Simuler un traitement de paiement (toujours réussi dans cet exemple)
        System.out.println("Traitement du paiement...");
        System.out.println("Numéro de carte : " + cardNumber);
        System.out.println("Date d'expiration : " + expirationDate);
        System.out.println("Code de sécurité : " + securityCode);
        System.out.println("Nom du titulaire : " + cardHolderName);

        return true; // Simuler un paiement réussi
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}