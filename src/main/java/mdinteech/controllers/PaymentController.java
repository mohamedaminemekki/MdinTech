package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;
import mdinteech.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class PaymentController {

    @FXML
    private Label paymentDetailsLabel;

    @FXML
    private TextField cardNumberField;

    @FXML
    private DatePicker expirationDatePicker;

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
        // Vérifier les champs du formulaire
        if (!validateCardNumber() || !validateExpirationDate() || !validateSecurityCode() || !validateCardHolderName()) {
            return;
        }

        // Simuler un paiement réussi ou échoué
        boolean paymentSuccess = simulatePayment();

        if (paymentSuccess) {
            try {
                // Mettre à jour le statut de paiement et le statut de la réservation
                reservation.setPaymentStatus("Paid"); // Statut de paiement mis à jour
                reservation.setStatus("Confirmed"); // Statut de la réservation mis à "Confirmed" si le paiement est réussi

                // Si la réservation n'existe pas encore dans la base de données, l'ajouter
                if (reservation.getId() == 0) { // ID = 0 signifie que la réservation n'a pas encore été enregistrée
                    reservationService.add(reservation);
                    System.out.println("Réservation ajoutée avec succès !");
                } else {
                    // Si la réservation existe déjà, la mettre à jour
                    reservationService.update(reservation);
                    System.out.println("Réservation mise à jour avec succès !");
                }

                // Afficher un message de succès
                showAlert("Paiement réussi", "Votre paiement a été confirmé avec succès !");

                // Fermer la fenêtre de paiement
                Stage stage = (Stage) confirmPaymentButton.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement de la réservation.");
            }
        } else {
            showAlert("Paiement échoué", "Le paiement n'a pas pu être traité. Veuillez réessayer.");
        }
    }
    private boolean validateCardNumber() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", ""); // Supprimer les espaces
        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            showAlert("Erreur", "Le numéro de carte doit contenir exactement 16 chiffres.");
            return false;
        }
        return true;
    }

    private boolean validateExpirationDate() {
        LocalDate expirationDate = expirationDatePicker.getValue();
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            showAlert("Erreur", "La date d'expiration doit être dans le futur.");
            return false;
        }
        return true;
    }

    private boolean validateSecurityCode() {
        String securityCode = securityCodeField.getText();
        if (securityCode.length() != 3 || !securityCode.matches("\\d+")) {
            showAlert("Erreur", "Le code de sécurité doit contenir exactement 3 chiffres.");
            return false;
        }
        return true;
    }

    private boolean validateCardHolderName() {
        String cardHolderName = cardHolderNameField.getText();
        if (cardHolderName.isEmpty() || !cardHolderName.matches("[a-zA-Z\\s]+")) {
            showAlert("Erreur", "Le nom du titulaire ne doit contenir que des lettres et des espaces.");
            return false;
        }
        return true;
    }

    private boolean simulatePayment() {
        // Simuler un paiement réussi dans 80% des cas
        return Math.random() < 0.8;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}