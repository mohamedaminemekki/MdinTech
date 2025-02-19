package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ReponseServices;

import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterReponse {
    @FXML
    private TextArea messageField;

    @FXML
    private Button btnEnvoyer;

    private final ReponseServices reponseService = new ReponseServices();
    private int reclamationId; // To store the selected reclamation ID

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    @FXML
    private void initialize() {
        btnEnvoyer.setOnAction(event -> ajouterReponse());
    }

    private void ajouterReponse() {
        String message = messageField.getText().trim();

        if (message.isEmpty()) {
            afficherAlerte("Erreur", "Le message ne peut pas être vide.");
            return;
        }

        // Create a response with today's date
        Reponse reponse = new Reponse(reclamationId, message, LocalDate.now().toString());

        try {
            reponseService.add(reponse);
            afficherAlerte("Succès", "Réponse envoyée avec succès !");

            // Close the window
            Stage stage = (Stage) btnEnvoyer.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible d'envoyer la réponse: " + e.getMessage());
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
