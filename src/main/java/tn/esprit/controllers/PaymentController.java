package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Facture;
import tn.esprit.services.FactureServices;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

public class PaymentController {

    // Champs FXML existants
    @FXML private Label montantLabel;
    @FXML private ComboBox<String> monthCombo;
    @FXML private ComboBox<String> yearCombo;
    @FXML private TextField nomCarteField;
    @FXML private TextField numeroCarteField;
    @FXML private PasswordField cvvField;

    private Facture facture;
    private Runnable refreshCallback;
    private final FactureServices factureService = new FactureServices();

    public void initializeData(Facture facture, Runnable callback) {
        this.facture = facture;
        this.refreshCallback = callback;
        initializeMontant();
        initializeDateInputs();
    }

    private void initializeMontant() {
        montantLabel.setText(String.format("Montant à payer : %.3f TND", facture.getPrixFact()));
    }

    private void initializeDateInputs() {
        monthCombo.getItems().clear();
        for(int i = 1; i <= 12; i++) {
            monthCombo.getItems().add(String.format("%02d", i));
        }

        yearCombo.getItems().clear();
        int currentYear = YearMonth.now().getYear();
        for(int i = currentYear; i < currentYear + 5; i++) {
            yearCombo.getItems().add(String.valueOf(i));
        }
    }

    @FXML
    private void handlePaymentConfirmation() {
        if(validateForm()) {
            processPayment();
        }
    }

    private void processPayment() {
        try {
            updateFactureStatus();
            generateReceipt();
            closeWindow();
            refreshCallback.run();
        } catch (Exception e) {
            showAlert("Erreur", "Échec du traitement du paiement : " + e.getMessage());
        }
    }

    private void updateFactureStatus() throws Exception {
        facture.setDatePaiement(LocalDate.now());
        facture.setState(true);
        factureService.updatePaymentStatus(facture);
    }

    private void generateReceipt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/Receipt.fxml"));
            Parent root = loader.load(); // Load doit être appelé avant getController()

            ReceiptController controller = loader.getController();
            String lastDigits = numeroCarteField.getText().length() > 4
                    ? numeroCarteField.getText().substring(numeroCarteField.getText().length() - 4)
                    : numeroCarteField.getText();

            controller.setPaymentDetails(facture, "Carte Bancaire", lastDigits);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Échec de génération du reçu");
        }
    }

    private boolean validateForm() {
        if(!validateField(nomCarteField, "Nom sur la carte")) return false;
        if(!validateCardNumber()) return false;
        if(!validateDateSelection()) return false;
        if(!validateCVV()) return false;
        return true;
    }

    private boolean validateCardNumber() {
        String cardNumber = numeroCarteField.getText().replaceAll("\\s+", "");
        if(!cardNumber.matches("\\d{16}")) {
            showAlert("Erreur", "Numéro de carte invalide (16 chiffres requis)");
            return false;
        }
        return true;
    }

    private boolean validateDateSelection() {
        if(monthCombo.getValue() == null || yearCombo.getValue() == null) {
            showAlert("Erreur", "Sélectionnez la date d'expiration");
            return false;
        }
        return true;
    }

    private boolean validateCVV() {
        if(!cvvField.getText().matches("\\d{3}")) {
            showAlert("Erreur", "Code CVV invalide (3 chiffres requis)");
            return false;
        }
        return true;
    }

    private boolean validateField(TextField field, String fieldName) {
        if(field.getText().trim().isEmpty()) {
            showAlert("Erreur", fieldName + " est requis");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) montantLabel.getScene().getWindow();
        stage.close();
    }
}