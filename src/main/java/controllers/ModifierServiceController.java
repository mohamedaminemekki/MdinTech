package controllers;

import entities.ServiceHospitalier;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceHospitalierServices;
import java.sql.SQLException;

public class ModifierServiceController {

    @FXML
    private TextField nomServiceField;

    @FXML
    private TextField descriptionField;

    private ServiceHospitalierServices serviceHospitalierServices = new ServiceHospitalierServices();
    private ServiceHospitalier serviceAModifier;
    private Stage stage;

    public void setService(ServiceHospitalier service, Stage stage) {
        this.serviceAModifier = service;
        this.stage = stage;
        // Pré-remplir les champs avec les données du service sélectionné
        nomServiceField.setText(service.getNomService());
        descriptionField.setText(service.getDescription());
    }

    @FXML
    private void handleSave() {
        try {
            // Mettre à jour les informations du service
            serviceAModifier.setNomService(nomServiceField.getText());
            serviceAModifier.setDescription(descriptionField.getText());

            // Appel du service pour la mise à jour
            serviceHospitalierServices.update(serviceAModifier);

            showAlert("Succès", "Service modifié avec succès.", Alert.AlertType.INFORMATION);
            stage.close(); // Fermer la fenêtre après modification

        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de modifier le service.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close(); // Fermer la fenêtre sans enregistrer
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
