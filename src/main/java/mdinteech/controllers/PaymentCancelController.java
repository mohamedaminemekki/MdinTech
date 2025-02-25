package mdintech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class PaymentCancelController {

    @FXML
    private Label cancelMessageLabel;

    public void initialize() {
        // Afficher un message d'annulation
        cancelMessageLabel.setText("Paiement annulé. Veuillez réessayer.");
    }

    @FXML
    private void handleReturnHome() {
        try {
            // Charger la vue de l'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/payment_cancel.fxml"));
            Parent root = loader.load();

            // Afficher la vue
            Stage stage = (Stage) cancelMessageLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}