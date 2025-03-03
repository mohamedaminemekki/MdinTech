package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class PaymentSuccessController {

    @FXML
    private Label successMessageLabel;

    public void initialize() {
        // Afficher un message de succès
        successMessageLabel.setText("Paiement réussi ! Merci pour votre réservation.");
    }

    @FXML
    private void handleReturnHome() {
        try {
            // Charger la vue de l'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/payment_success.fxml"));
            Parent root = loader.load();

            // Afficher la vue
            Stage stage = (Stage) successMessageLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}