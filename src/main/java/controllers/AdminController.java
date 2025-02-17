package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import entities.ServiceHospitalier;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import services.ServiceHospitalierServices;

import java.io.IOException;
import java.sql.SQLException;

public class AdminController {

    @FXML
    private ListView<ServiceHospitalier> serviceListView;

    @FXML
    private Button addServiceButton;
    @FXML
    private Button doctorListButton;

    @FXML
    private Button appointmentListButton;


    private ServiceHospitalierServices serviceHospitalierServices = new ServiceHospitalierServices();

    @FXML
    public void initialize() {
        // Charger les services dans la ListView
        loadServices();

        // Gestion du bouton Ajouter Service
        addServiceButton.setOnAction(this::handleAddService);
        doctorListButton.setOnAction(this::handleShowDoctors);
        appointmentListButton.setOnAction(this::handleShowAppointments);

    }

    // Méthode pour charger la liste des services
    private void loadServices() {
        try {
            var services = serviceHospitalierServices.readList();

            serviceListView.setCellFactory(param -> new ListCell<ServiceHospitalier>() {
                @Override
                protected void updateItem(ServiceHospitalier item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        HBox hbox = new HBox(10); // Espacement de 10px entre les éléments
                        hbox.setPrefWidth(780); // Largeur fixe pour une disposition uniforme

                        Label serviceDetails = new Label("ID: " + item.getIdService() + " | Nom: " + item.getNomService() + " | Description: " + item.getDescription());
                        serviceDetails.getStyleClass().add("list-cell-label");

                        // Créer un espace flexible pour aligner les boutons à droite
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        Button modifyButton = new Button("Modifier");
                        Button deleteButton = new Button("Supprimer");

                        // Ajouter des classes CSS pour uniformiser les boutons
                        modifyButton.getStyleClass().add("modify-button");
                        deleteButton.getStyleClass().add("delete-button");

                        // Définir les actions des boutons
                        modifyButton.setOnAction(event -> handleModifyService(item));
                        deleteButton.setOnAction(event -> handleDeleteService(item.getIdService()));

                        // Ajouter les éléments à la HBox
                        hbox.getChildren().addAll(serviceDetails, spacer, modifyButton, deleteButton);

                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });

            serviceListView.getItems().setAll(services);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les services.", Alert.AlertType.ERROR);
        }
    }


    // Méthode pour gérer l'ajout d'un service
    private void handleAddService(ActionEvent event) {
        try {
            // Charger le FXML de la fenêtre d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterService.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ajouter un service");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout. Détails : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Méthode pour gérer la suppression d'un service
    private void handleDeleteService(int idService) {
        try {
            serviceHospitalierServices.delete(idService); // Appeler la méthode delete
            loadServices(); // Recharger la liste des services après suppression
            showAlert("Succès", "Le service a été supprimé avec succès.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression du service.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Méthode pour gérer la modification d'un service
    private void handleModifyService(ServiceHospitalier service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierService.fxml"));
            Scene scene = new Scene(loader.load());

            ModifierServiceController controller = loader.getController();
            Stage stage = new Stage();
            controller.setService(service, stage);

            stage.setScene(scene);
            stage.setTitle("Modifier le service");
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void handleShowDoctors(ActionEvent event) {
        openWindow("/listeMedecins.fxml", "Liste des médecins");
    }

    // Méthode pour afficher la liste des rendez-vous
    private void handleShowAppointments(ActionEvent event) {
        openWindow("/listeRendezVous.fxml", "Liste des rendez-vous");
    }

    // Méthode générique pour ouvrir une nouvelle fenêtre
    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre : " + title, Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    // Méthode pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
