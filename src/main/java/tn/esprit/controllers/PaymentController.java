package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Facture;
import tn.esprit.services.FactureServices;

import java.time.LocalDate;
import java.time.YearMonth;

public class PaymentController {

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

        // Initialiser le montant
        montantLabel.setText(String.format("%.2f TND", facture.getPrixFact()));

        // Remplir les combobox
        initializeDateInputs();
    }

    private void initializeDateInputs() {
        // Mois (01 à 12)
        for(int i = 1; i <= 12; i++) {
            monthCombo.getItems().add(String.format("%02d", i));
        }

        // Années (courante + 5 ans)
        int currentYear = YearMonth.now().getYear();
        for(int i = currentYear; i < currentYear + 5; i++) {
            yearCombo.getItems().add(String.valueOf(i));
        }
    }

    @FXML
    private void handlePaymentConfirmation() {
        try {
            // Validation des champs
            if (!validateForm()) return;

            // Créer la date d'expiration
            YearMonth expirationDate = YearMonth.of(
                    Integer.parseInt(yearCombo.getValue()),
                    Integer.parseInt(monthCombo.getValue())
            );

            // Mettre à jour la facture avec la date actuelle
            facture.setDatePaiement(LocalDate.now()); // Correction clé ici
            facture.setState(true);

            factureService.updatePaymentStatus(facture);

            // Fermer la fenêtre
            closeWindow();

            // Rafraîchir le tableau
            if (refreshCallback != null) refreshCallback.run();

            showSuccessAlert();

        } catch (Exception e) {
            showErrorAlert("Erreur : " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if(nomCarteField.getText().isEmpty()
                || numeroCarteField.getText().isEmpty()
                || monthCombo.getValue() == null
                || yearCombo.getValue() == null
                || cvvField.getText().isEmpty()) {

            showErrorAlert("Tous les champs marqués (*) sont obligatoires !");
            return false;
        }

        if(!numeroCarteField.getText().matches("\\d{16}")) {
            showErrorAlert("Numéro de carte invalide (16 chiffres requis)");
            return false;
        }

        if(!cvvField.getText().matches("\\d{3}")) {
            showErrorAlert("CVV invalide (3 chiffres requis)");
            return false;
        }

        return true;
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement réussi");
        alert.setHeaderText(null);
        alert.setContentText("Paiement confirmé !\nRéférence: FACT-" + facture.getId());
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    private void closeWindow() {
        ((Stage) montantLabel.getScene().getWindow()).close();
    }
}