package controllers;

import entities.ServiceHospitalier;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceHospitalierServices;

import java.sql.SQLException;

public class AddServiceController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    private ServiceHospitalierServices serviceHospitalierServices = new ServiceHospitalierServices();

    // Méthode pour gérer l'ajout du service
    @FXML
    public void handleAddService() {
        /*String name = nameField.getText();
        String description = descriptionField.getText();

        if (name.isEmpty() || description.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        ServiceHospitalier service = new ServiceHospitalier(name, description);

        try {
            serviceHospitalierServices.add(service); // Ajouter le service
            showAlert("Succès", "Service ajouté avec succès !");
            closeWindow(); // Fermer la fenêtre après l'ajout
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout du service.");
            e.printStackTrace();
        }*/
    }

    // Méthode pour fermer la fenêtre d'ajout
    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        // Obtenez l'objet Stage à partir de la fenêtre actuelle
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.hide();  // Fermer la fenêtre
    }


    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
