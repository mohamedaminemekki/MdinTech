package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.RendezVousServices;
import entities.RendezVous;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class ListeRendezVousController {

    @FXML
    private ListView<RendezVous> rendezVousListView;

    @FXML
    private Button addRendezVousButton, backButton;

    private final RendezVousServices rendezVousServices = new RendezVousServices();

    @FXML
    public void initialize() {
        loadRendezVous();

        // Bouton "Ajouter Rendez-vous"
        addRendezVousButton.setOnAction(event -> ajouterRendezVous());

        // Bouton "Retour"
        backButton.setOnAction(event -> retournerAdmin());
    }

    private void loadRendezVous() {
        try {
            rendezVousListView.getItems().setAll(rendezVousServices.readList());
            rendezVousListView.setCellFactory(param -> new RendezVousCell());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ajouterRendezVous() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Rendez-vous");
        dialog.setHeaderText("Veuillez entrer les informations du rendez-vous");

        // Ajout des boutons
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Création des champs
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        DatePicker dateField = new DatePicker();
        dateField.setPromptText("Date");
        TextField timeField = new TextField();
        timeField.setPromptText("Heure (HH:mm)");
        TextField lieuField = new TextField();
        lieuField.setPromptText("Lieu");
        TextField statusField = new TextField();
        statusField.setPromptText("Statut");
        TextField idMedecinField = new TextField();
        idMedecinField.setPromptText("ID du Médecin");

        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Heure:"), 0, 1);
        grid.add(timeField, 1, 1);
        grid.add(new Label("Lieu:"), 0, 2);
        grid.add(lieuField, 1, 2);
        grid.add(new Label("Statut:"), 0, 3);
        grid.add(statusField, 1, 3);
        grid.add(new Label("ID Médecin:"), 0, 4);
        grid.add(idMedecinField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Désactiver la fermeture automatique lors du clic sur "Ajouter"
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            LocalDate date = dateField.getValue();
            String timeText = timeField.getText().trim();
            String lieu = lieuField.getText().trim();
            String status = statusField.getText().trim();
            String idMedecinText = idMedecinField.getText().trim();

            // Vérification des champs vides
            if (date == null || timeText.isEmpty() || lieu.isEmpty() || status.isEmpty() || idMedecinText.isEmpty()) {
                showAlert("Champs vides", "Tous les champs sont obligatoires !");
                event.consume(); // Empêcher la fermeture du dialogue
                return;
            }

            // Vérification des valeurs
            if (!timeText.matches("\\d{2}:\\d{2}")) {
                showAlert("Erreur", "Le champ 'Heure' doit être au format HH:mm !");
                event.consume();
                return;
            }
            if (!idMedecinText.matches("\\d+")) {
                showAlert("Erreur", "Le champ 'ID Médecin' doit contenir uniquement des chiffres !");
                event.consume();
                return;
            }

            // Conversion et ajout du rendez-vous
            LocalTime time = LocalTime.parse(timeText);
            int idMedecin = Integer.parseInt(idMedecinText);

            RendezVous rendezVous = new RendezVous(0, date, time, lieu, status, idMedecin);

            try {
                rendezVousServices.add(rendezVous);
                showAlert("Succès", "Rendez-vous ajouté avec succès !");
                loadRendezVous(); // Rafraîchir la liste
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible d'ajouter le rendez-vous.");
                e.printStackTrace();
            }
        });

        dialog.showAndWait();
    }

    private void modifierRendezVous(RendezVous rendezVous) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier un Rendez-vous");
        dialog.setHeaderText("Modification des informations du rendez-vous");

        // Ajout des boutons
        ButtonType updateButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Création des champs avec valeurs pré-remplies
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        DatePicker dateField = new DatePicker(rendezVous.getDateRendezVous());
        TextField timeField = new TextField(rendezVous.getTimeRendezVous().toString());
        TextField lieuField = new TextField(rendezVous.getLieu());
        TextField statusField = new TextField(rendezVous.getStatus());
        TextField idMedecinField = new TextField(String.valueOf(rendezVous.getIdMedecin()));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Heure:"), 0, 1);
        grid.add(timeField, 1, 1);
        grid.add(new Label("Lieu:"), 0, 2);
        grid.add(lieuField, 1, 2);
        grid.add(new Label("Statut:"), 0, 3);
        grid.add(statusField, 1, 3);
        grid.add(new Label("ID Médecin:"), 0, 4);
        grid.add(idMedecinField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Désactiver la fermeture automatique lors du clic sur "Modifier"
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.addEventFilter(ActionEvent.ACTION, event -> {
            LocalDate date = dateField.getValue();
            String timeText = timeField.getText().trim();
            String lieu = lieuField.getText().trim();
            String status = statusField.getText().trim();
            String idMedecinText = idMedecinField.getText().trim();

            // Vérification des champs vides
            if (date == null || timeText.isEmpty() || lieu.isEmpty() || status.isEmpty() || idMedecinText.isEmpty()) {
                showAlert("Champs vides", "Tous les champs sont obligatoires !");
                event.consume();
                return;
            }

            // Vérification des valeurs
            if (!timeText.matches("\\d{2}:\\d{2}")) {
                showAlert("Erreur", "Le champ 'Heure' doit être au format HH:mm !");
                event.consume();
                return;
            }
            if (!idMedecinText.matches("\\d+")) {
                showAlert("Erreur", "Le champ 'ID Médecin' doit contenir uniquement des chiffres !");
                event.consume();
                return;
            }

            // Conversion et mise à jour du rendez-vous
            LocalTime time = LocalTime.parse(timeText);
            int idMedecin = Integer.parseInt(idMedecinText);

            rendezVous.setDateRendezVous(date);
            rendezVous.setTimeRendezVous(time);
            rendezVous.setLieu(lieu);
            rendezVous.setStatus(status);
            rendezVous.setIdMedecin(idMedecin);

            try {
                rendezVousServices.update(rendezVous);
                showAlert("Succès", "Rendez-vous mis à jour avec succès !");
                loadRendezVous(); // Rafraîchir la liste
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de modifier le rendez-vous.");
                e.printStackTrace();
            }
        });

        dialog.showAndWait();
    }

    private void supprimerRendezVous(RendezVous rendezVous) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le rendez-vous ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le rendez-vous du " + rendezVous.getDateRendezVous() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rendezVousServices.delete(rendezVous.getIdRendezVous());
                loadRendezVous();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void retournerAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour afficher une alerte sans fermer l'interface
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe interne pour personnaliser l'affichage des rendez-vous.
     */
    private class RendezVousCell extends ListCell<RendezVous> {
        private final GridPane gridPane = new GridPane();
        private final Label dateLabel = new Label();
        private final Label timeLabel = new Label();
        private final Label lieuLabel = new Label();
        private final Label statusLabel = new Label();
        private final Label medecinLabel = new Label();
        private final Button editButton = new Button("Modifier");
        private final Button deleteButton = new Button("Supprimer");

        public RendezVousCell() {
            gridPane.setHgap(10);
            gridPane.setVgap(5);
            gridPane.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));

            // Style des labels
            dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            timeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            lieuLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            medecinLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

            // Style des boutons
            editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

            // Ajout des éléments à la grille
            gridPane.add(dateLabel, 0, 0);
            gridPane.add(timeLabel, 1, 0);
            gridPane.add(lieuLabel, 2, 0);
            gridPane.add(statusLabel, 3, 0);
            gridPane.add(medecinLabel, 4, 0);
            gridPane.add(editButton, 5, 0);
            gridPane.add(deleteButton, 6, 0);

            // Actions des boutons
            editButton.setOnAction(event -> {
                RendezVous rendezVous = getItem();
                if (rendezVous != null) {
                    modifierRendezVous(rendezVous);
                }
            });

            deleteButton.setOnAction(event -> {
                RendezVous rendezVous = getItem();
                if (rendezVous != null) {
                    supprimerRendezVous(rendezVous);
                }
            });
        }

        @Override
        protected void updateItem(RendezVous rendezVous, boolean empty) {
            super.updateItem(rendezVous, empty);
            if (empty || rendezVous == null) {
                setGraphic(null);
            } else {
                dateLabel.setText("📅 " + rendezVous.getDateRendezVous());
                timeLabel.setText("⏰ " + rendezVous.getTimeRendezVous());
                lieuLabel.setText("📍 " + rendezVous.getLieu());
                statusLabel.setText("📋 " + rendezVous.getStatus());
                medecinLabel.setText("👨‍⚕️ ID Médecin: " + rendezVous.getIdMedecin());
                setGraphic(gridPane);
            }
        }
    }
}