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
import services.MedecinServices;
import entities.Medecin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ListeMedecinsController {

    @FXML
    private ListView<Medecin> medecinListView;

    @FXML
    private Button addDoctorButton, backButton;

    private final MedecinServices medecinServices = new MedecinServices();

    @FXML
    public void initialize() {
        loadMedecins();

        // Bouton "Ajouter Médecin"
        addDoctorButton.setOnAction(event -> ajouterMedecin());

        // Bouton "Retour"
        backButton.setOnAction(event -> retournerAdmin());
    }

    private void loadMedecins() {
        try {
            medecinListView.getItems().setAll(medecinServices.readList());
            medecinListView.setCellFactory(param -> new MedecinCell());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ajouterMedecin() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Médecin");
        dialog.setHeaderText("Veuillez entrer les informations du médecin");

        // Ajout des boutons
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Création des champs
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        TextField specialiteField = new TextField();
        specialiteField.setPromptText("Spécialité");
        TextField contactField = new TextField();
        contactField.setPromptText("Contact (Numéro)");
        TextField idServiceField = new TextField();
        idServiceField.setPromptText("ID du Service");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Spécialité:"), 0, 2);
        grid.add(specialiteField, 1, 2);
        grid.add(new Label("Contact:"), 0, 3);
        grid.add(contactField, 1, 3);
        grid.add(new Label("ID Service:"), 0, 4);
        grid.add(idServiceField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Désactiver la fermeture automatique lors du clic sur "Ajouter"
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String specialite = specialiteField.getText().trim();
            String contactText = contactField.getText().trim();
            String idServiceText = idServiceField.getText().trim();

            // Vérification des champs vides
            if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty() || contactText.isEmpty() || idServiceText.isEmpty()) {
                showAlert("Champs vides", "Tous les champs sont obligatoires !");
                event.consume(); // Empêcher la fermeture du dialogue
                return;
            }

            // Vérification des valeurs
            if (!nom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                showAlert("Erreur", "Le champ 'Nom' ne doit pas contenir de chiffres !");
                event.consume();
                return;
            }
            if (!prenom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                showAlert("Erreur", "Le champ 'Prénom' ne doit pas contenir de chiffres !");
                event.consume();
                return;
            }
            if (!specialite.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                showAlert("Erreur", "Le champ 'Spécialité' ne doit pas contenir de chiffres !");
                event.consume();
                return;
            }
            if (!contactText.matches("\\d+")) {
                showAlert("Erreur", "Le champ 'Contact' doit contenir uniquement des chiffres !");
                event.consume();
                return;
            }
            if (!idServiceText.matches("\\d+")) {
                showAlert("Erreur", "Le champ 'ID Service' doit contenir uniquement des chiffres !");
                event.consume();
                return;
            }

            // Conversion et ajout du médecin
            int contact = Integer.parseInt(contactText);
            int idService = Integer.parseInt(idServiceText);

            Medecin medecin = new Medecin(0, nom, prenom, specialite, contact, idService);

            try {
                medecinServices.add(medecin);
                showAlert("Succès", "Médecin ajouté avec succès !");
                loadMedecins(); // Rafraîchir la liste
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible d'ajouter le médecin.");
                e.printStackTrace();
            }
        });

        dialog.showAndWait();
    }


    private void modifierMedecin(Medecin medecin) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier un Médecin");
        dialog.setHeaderText("Modification des informations du médecin");

        // Ajout des boutons
        ButtonType updateButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Création des champs avec valeurs pré-remplies
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomField = new TextField(medecin.getNomM());
        TextField prenomField = new TextField(medecin.getPrenomM());
        TextField specialiteField = new TextField(medecin.getSpecialite());
        TextField contactField = new TextField(String.valueOf(medecin.getContact()));
        TextField idServiceField = new TextField(String.valueOf(medecin.getIdService()));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Spécialité:"), 0, 2);
        grid.add(specialiteField, 1, 2);
        grid.add(new Label("Contact:"), 0, 3);
        grid.add(contactField, 1, 3);
        grid.add(new Label("ID Service:"), 0, 4);
        grid.add(idServiceField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Désactiver la fermeture automatique lors du clic sur "Modifier"
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.addEventFilter(ActionEvent.ACTION, event -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String specialite = specialiteField.getText().trim();
            String contactText = contactField.getText().trim();
            String idServiceText = idServiceField.getText().trim();

            // Vérification des champs vides
            if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty() || contactText.isEmpty() || idServiceText.isEmpty()) {
                showAlert("Champs vides", "Tous les champs sont obligatoires !");
                event.consume();
                return;
            }

            // Vérification des valeurs
            if (!nom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                showAlert("Erreur", "Le champ 'Nom' ne doit pas contenir de chiffres !");
                event.consume();
                return;
            }
            if (!prenom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                showAlert("Erreur", "Le champ 'Prénom' ne doit pas contenir de chiffres !");
                event.consume();
                return;
            }
            if (!specialite.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                showAlert("Erreur", "Le champ 'Spécialité' ne doit pas contenir de chiffres !");
                event.consume();
                return;
            }
            if (!contactText.matches("\\d+")) {
                showAlert("Erreur", "Le champ 'Contact' doit contenir uniquement des chiffres !");
                event.consume();
                return;
            }
            if (!idServiceText.matches("\\d+")) {
                showAlert("Erreur", "Le champ 'ID Service' doit contenir uniquement des chiffres !");
                event.consume();
                return;
            }

            // Conversion et mise à jour du médecin
            int contact = Integer.parseInt(contactText);
            int idService = Integer.parseInt(idServiceText);

            medecin.setNomM(nom);
            medecin.setPrenomM(prenom);
            medecin.setSpecialite(specialite);
            medecin.setContact(contact);
            medecin.setIdService(idService);

            try {
                medecinServices.update(medecin);
                showAlert("Succès", "Médecin mis à jour avec succès !");
                loadMedecins(); // Rafraîchir la liste
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de modifier le médecin.");
                e.printStackTrace();
            }
        });

        dialog.showAndWait();
    }

    // Méthode pour afficher une alerte sans fermer l'interface
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void supprimerMedecin(Medecin medecin) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le médecin ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + medecin.getNomM() + " " + medecin.getPrenomM() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                medecinServices.delete(medecin.getIdMedecin());
                loadMedecins();
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

    /**
     * Classe interne pour personnaliser l'affichage des médecins.
     */
    private class MedecinCell extends ListCell<Medecin> {
        private final GridPane gridPane = new GridPane();
        private final Label nomLabel = new Label();
        private final Label specialiteLabel = new Label();
        private final Label contactLabel = new Label();
        private final Label serviceLabel = new Label();
        private final Button editButton = new Button("Modifier");
        private final Button deleteButton = new Button("Supprimer");

        public MedecinCell() {
            gridPane.setHgap(10);
            gridPane.setVgap(5);
            gridPane.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));

            // Style des labels
            nomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            specialiteLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            contactLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            serviceLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

            // Style des boutons
            editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

            // Ajout des éléments à la grille
            gridPane.add(nomLabel, 0, 0);
            gridPane.add(specialiteLabel, 1, 0);
            gridPane.add(contactLabel, 2, 0);
            gridPane.add(serviceLabel, 3, 0);
            gridPane.add(editButton, 4, 0);
            gridPane.add(deleteButton, 5, 0);

            // Actions des boutons
            editButton.setOnAction(event -> {
                Medecin medecin = getItem();
                if (medecin != null) {
                    modifierMedecin(medecin);
                }
            });

            deleteButton.setOnAction(event -> {
                Medecin medecin = getItem();
                if (medecin != null) {
                    supprimerMedecin(medecin);
                }
            });
        }

        @Override
        protected void updateItem(Medecin medecin, boolean empty) {
            super.updateItem(medecin, empty);
            if (empty || medecin == null) {
                setGraphic(null);
            } else {
                nomLabel.setText("👨‍⚕️ " + medecin.getNomM() + " " + medecin.getPrenomM());
                specialiteLabel.setText("🔹 Spécialité: " + medecin.getSpecialite());
                contactLabel.setText("📞 Contact: " + medecin.getContact());
                serviceLabel.setText("🏥 ID Service: " + medecin.getIdService());
                setGraphic(gridPane);
            }
        }
    }
}
