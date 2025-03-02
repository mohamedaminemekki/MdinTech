package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.ServiceHospitalierServices;
import entities.ServiceHospitalier;

import java.io.IOException;
import java.sql.SQLException;

public class AdminController {


    @FXML
    private ListView<ServiceHospitalier> serviceListView;

    @FXML
    private Button addServiceButton, doctorListButton, appointmentListButton;

    private ServiceHospitalierServices serviceHospitalierServices = new ServiceHospitalierServices();

    @FXML
    public void initialize() {
        loadServices();
        statsButton.setOnAction(this::showStatistics);


        // Gestion des boutons

        addServiceButton.setOnAction(this::handleAddService);
        doctorListButton.setOnAction(event -> openWindow("/listeMedecins.fxml", "Liste des médecins"));
        appointmentListButton.setOnAction(event -> openWindow("/listeRendezVous.fxml", "Liste des rendez-vous"));
    }
    @FXML
    private Button statsButton;

    @FXML
    private void showStatistics(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatsView.fxml"));
            Parent root = loader.load();

            Stage statsStage = new Stage();
            statsStage.setTitle("📊 Statistique des Rendez-vous");
            statsStage.setScene(new Scene(root));
            statsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadServices() {
        try {
            var services = serviceHospitalierServices.readList();
            serviceListView.setCellFactory(param -> new ServiceCell()); // Utilisation de ServiceCell
            serviceListView.getItems().setAll(services);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les services.", Alert.AlertType.ERROR);
        }
    }

    private void handleAddService(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Service");
        dialog.setHeaderText("Veuillez entrer les informations du service");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom du Service");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        grid.add(new Label("Nom du Service:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, e -> {
            String nom = nomField.getText().trim();
            String description = descriptionField.getText().trim();

            // Contrôles de saisie (pas de chiffres ni caractères invalides)
            if (nom.isEmpty() || description.isEmpty()) {
                showAlert("Champs vides", "Tous les champs sont obligatoires !", Alert.AlertType.ERROR);
                e.consume(); // Empêche la fermeture du dialogue
                return;
            }

            if (!nom.matches("^[A-Za-zÀ-ÿ\\s\\-.,!?:;()]+$")) {
                showAlert("Erreur", "Le nom du service ne doit contenir que des lettres et des caractères spéciaux valides.", Alert.AlertType.ERROR);
                e.consume();
                return;
            }

            if (!description.matches("^[A-Za-zÀ-ÿ\\s\\-.,!?:;()]+$")) {
                showAlert("Erreur", "La description ne doit contenir que des lettres et des caractères spéciaux valides.", Alert.AlertType.ERROR);
                e.consume();
                return;
            }

            // Vérification de l'unicité du service
            try {
                if (serviceHospitalierServices.existsByName(nom)) {
                    showAlert("Erreur", "Ce service existe déjà.", Alert.AlertType.ERROR);
                    e.consume();
                    return;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try {
                ServiceHospitalier service = new ServiceHospitalier(0, nom, description);
                serviceHospitalierServices.add(service);
                showAlert("Succès", "Service ajouté avec succès !", Alert.AlertType.INFORMATION);
                loadServices();
            } catch (SQLException ex) {
                showAlert("Erreur", "Impossible d'ajouter le service.", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        dialog.showAndWait();
    }

    private void handleModifyService(ServiceHospitalier service) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier un Service");
        dialog.setHeaderText("Modification des informations du service");

        ButtonType updateButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomField = new TextField(service.getNomService());
        TextField descriptionField = new TextField(service.getDescription());

        grid.add(new Label("Nom du Service:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.addEventFilter(ActionEvent.ACTION, e -> {
            String nom = nomField.getText().trim();
            String description = descriptionField.getText().trim();

            // Contrôles de saisie (pas de chiffres ni caractères invalides)
            if (nom.isEmpty() || description.isEmpty()) {
                showAlert("Champs vides", "Tous les champs sont obligatoires !", Alert.AlertType.ERROR);
                e.consume(); // Empêche la fermeture du dialogue
                return;
            }

            if (!nom.matches("^[A-Za-zÀ-ÿ\\s\\-.,!?:;()]+$")) {
                showAlert("Erreur", "Le nom du service ne doit contenir que des lettres et des caractères spéciaux valides.", Alert.AlertType.ERROR);
                e.consume();
                return;
            }

            if (!description.matches("^[A-Za-zÀ-ÿ\\s\\-.,!?:;()]+$")) {
                showAlert("Erreur", "La description ne doit contenir que des lettres et des caractères spéciaux valides.", Alert.AlertType.ERROR);
                e.consume();
                return;
            }

            // Vérification de l'unicité du service (même nom, sauf pour le service actuel)
            try {
                if (serviceHospitalierServices.existsByName(nom) && !service.getNomService().equals(nom)) {
                    showAlert("Erreur", "Ce service existe déjà.", Alert.AlertType.ERROR);
                    e.consume();
                    return;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try {
                service.setNomService(nom);
                service.setDescription(description);
                serviceHospitalierServices.update(service);
                showAlert("Succès", "Service modifié avec succès !", Alert.AlertType.INFORMATION);
                loadServices();
            } catch (SQLException ex) {
                showAlert("Erreur", "Impossible de modifier le service.", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        dialog.showAndWait();
    }


    private void handleDeleteService(int idService) {
        try {
            serviceHospitalierServices.delete(idService);
            loadServices();
            showAlert("Succès", "Le service a été supprimé avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression du service.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

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

    private void showAlert(String title, String message, Alert.AlertType error) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class ServiceCell extends ListCell<ServiceHospitalier> {
        private final GridPane gridPane = new GridPane();
        private final Label nomLabel = new Label();
        private final Label descriptionLabel = new Label();
        private final Button editButton = new Button("Modifier");
        private final Button deleteButton = new Button("Supprimer");

        // Police et couleur de la liste des médecins
        private final Font police = Font.font("Arial", FontWeight.NORMAL, 12); // Exemples
        private final String couleurTexte = "#333333"; // Couleur de texte pour correspondre à celle des médecins

        public ServiceCell() {
            gridPane.setHgap(10);
            gridPane.setVgap(5);
            gridPane.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));

            // Style des labels
            nomLabel.setFont(police);
            nomLabel.setTextFill(javafx.scene.paint.Color.web(couleurTexte));

            descriptionLabel.setFont(police);
            descriptionLabel.setTextFill(javafx.scene.paint.Color.web(couleurTexte));

            // Style des boutons
            editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

            // Ajout des éléments à la grille
            gridPane.add(nomLabel, 0, 0);
            gridPane.add(descriptionLabel, 1, 0);
            gridPane.add(editButton, 2, 0);
            gridPane.add(deleteButton, 3, 0);

            // Actions des boutons
            editButton.setOnAction(event -> {
                ServiceHospitalier service = getItem();
                if (service != null) {
                    handleModifyService(service); // Appel à la méthode de modification
                }
            });

            deleteButton.setOnAction(event -> {
                ServiceHospitalier service = getItem();
                if (service != null) {
                    handleDeleteService(service.getIdService()); // Appel à la méthode de suppression
                }
            });
        }

        @Override
        protected void updateItem(ServiceHospitalier service, boolean empty) {
            super.updateItem(service, empty);
            if (empty || service == null) {
                setGraphic(null);
            } else {
                nomLabel.setText("Service: " + service.getNomService());
                descriptionLabel.setText("Description: " + service.getDescription());
                setGraphic(gridPane);
            }
        }
    }
}
