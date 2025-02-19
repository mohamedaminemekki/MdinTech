package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.ReclamationServices;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterReclamation {

    @FXML
    private TextField clientIdField;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private CheckBox stateCheckBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ImageView photoPreview;
    @FXML
    private Button uploadButton;
    @FXML
    private Button submitButton;
    @FXML
    private Button afficherReclamationsButton;

    private File selectedImageFile = null;

    @FXML
    public void initialize() {
        typeComboBox.setPromptText("Veuillez sélectionner le type");
        typeComboBox.getItems().addAll(
                "Problème d'application",
                "Réclamation service administratif",
                "Réclamation service de transport",
                "Réclamation service hospitalier",
                "Réclamation service supermarché en ligne",
                "Autre problème"
        );

        uploadButton.setOnAction(event -> choisirPhoto());
        submitButton.setOnAction(event -> soumettreReclamation());

        // Event handler for navigating to the reclamations list
        afficherReclamationsButton.setOnAction(event -> afficherReclamations());
    }

    private void choisirPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            photoPreview.setImage(new Image(file.toURI().toString()));
        }
    }

    private void soumettreReclamation() {
        try {
            if (clientIdField.getText().isEmpty() || descriptionField.getText().isEmpty() || dateField.getValue() == null || typeComboBox.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires !");
                return;
            }

            int clientId = Integer.parseInt(clientIdField.getText());
            LocalDate date = dateField.getValue();
            String description = descriptionField.getText();
            String type = typeComboBox.getValue();
            String photoPath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "Aucune photo";

            Reclamation reclamation = new Reclamation(clientId, date.toString(), description, false, type, photoPath);
            ReclamationServices reclamationService = new ReclamationServices();
            reclamationService.add(reclamation);

            showAlert("Succès", "Réclamation ajoutée avec succès !");

            // 🔹 Réinitialiser les champs après ajout
            clientIdField.clear();
            descriptionField.clear();
            dateField.setValue(null);
            typeComboBox.getSelectionModel().clearSelection();
            photoPreview.setImage(null);
            selectedImageFile = null;

        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID du client doit être un nombre valide !");
        } catch (SQLException e) {
            showAlert("Erreur", "Problème lors de l'ajout : " + e.getMessage());
        }
    }


    private void afficherReclamations() {
        try {
            // Load the "AfficherReclamationClient.fxml" for viewing reclamations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamationClient.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) afficherReclamationsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème de navigation : " + e.getMessage());
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
