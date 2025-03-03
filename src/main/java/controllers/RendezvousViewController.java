package controllers;

import entities.RendezVous;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import services.RendezVousServices;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class RendezvousViewController {

    @FXML
    private ListView<RendezVous> appointmentsListView;

    private final RendezVousServices rendezVousServices = new RendezVousServices();

    @FXML
    public void initialize() {
        loadAppointments();
        appointmentsListView.setCellFactory(param -> new RendezVousCell());
    }

    // Charger les rendez-vous dans la ListView
    private void loadAppointments() {
        try {
            ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList(rendezVousServices.readList());
            appointmentsListView.setItems(rendezVousList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des rendez-vous.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Annuler un rendez-vous
    private void cancelAppointment(RendezVous rendezVous) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation d'annulation");
        alert.setHeaderText("Annuler le rendez-vous ?");
        alert.setContentText("Êtes-vous sûr de vouloir annuler le rendez-vous du " + rendezVous.getDateRendezVous() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rendezVous.setStatus("Annulé");
                rendezVousServices.update(rendezVous);
                loadAppointments(); // Rafraîchir la liste
                showAlert("Succès", "Le rendez-vous a été annulé.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de l'annulation du rendez-vous.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void modifyAppointment(RendezVous rendezVous) {
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

        // ComboBox pour le lieu (salle A, salle B, salle C)
        ComboBox<String> salleComboBox = new ComboBox<>();
        salleComboBox.getItems().addAll("Salle A", "Salle B", "Salle C");
        salleComboBox.setValue(rendezVous.getLieu()); // Valeur par défaut

        // DatePicker pour la date
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(rendezVous.getDateRendezVous()); // Valeur par défaut

        // Spinner pour l'heure et les minutes
        Spinner<Integer> heureSpinner = new Spinner<>(0, 23, rendezVous.getTimeRendezVous().getHour());
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, rendezVous.getTimeRendezVous().getMinute());

        // Ajout des champs à la grille
        grid.add(new Label("Lieu:"), 0, 0);
        grid.add(salleComboBox, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Heure:"), 0, 2);
        grid.add(heureSpinner, 1, 2);
        grid.add(new Label("Minute:"), 0, 3);
        grid.add(minuteSpinner, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Désactiver la fermeture automatique lors du clic sur "Modifier"
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                // Récupérer les nouvelles valeurs
                String salle = salleComboBox.getValue();
                LocalDate date = datePicker.getValue();
                int heure = heureSpinner.getValue();
                int minute = minuteSpinner.getValue();

                // Vérification des champs vides
                if (salle == null || date == null) {
                    throw new IllegalArgumentException("Tous les champs doivent être remplis !");
                }

                // Créer un objet LocalTime pour l'heure
                LocalTime time = LocalTime.of(heure, minute);

                // Vérification de la disponibilité du créneau
                if (rendezVousServices.isTimeSlotTaken(date, time)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Créneau Indisponible");
                    alert.setHeaderText("Ce créneau est déjà réservé.");
                    alert.setContentText("Veuillez choisir une autre heure.");
                    alert.showAndWait();
                    event.consume(); // Empêcher la fermeture du dialogue
                    return;
                }

                // Mise à jour du rendez-vous
                rendezVous.setLieu(salle);
                rendezVous.setDateRendezVous(date);
                rendezVous.setTimeRendezVous(time);

                // Mettre à jour le rendez-vous dans la base de données
                rendezVousServices.update(rendezVous);

                // Rafraîchir la liste des rendez-vous
                loadAppointments();

                // Afficher un message de succès
                showAlert("Succès", "Le rendez-vous a été modifié avec succès.", Alert.AlertType.INFORMATION);
            } catch (IllegalArgumentException e) {
                showAlert("Erreur de saisie", e.getMessage(), Alert.AlertType.WARNING);
                event.consume(); // Empêcher la fermeture du dialogue
            } catch (SQLException e) {
                showAlert("Erreur Base de Données", "Erreur lors de la modification du rendez-vous.", Alert.AlertType.ERROR);
                e.printStackTrace();
                event.consume(); // Empêcher la fermeture du dialogue
            }
        });

        dialog.showAndWait();
    }

    // Afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
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
        private final Label heureLabel = new Label();
        private final Label lieuLabel = new Label();
        private final Label statusLabel = new Label();
        private final Button editButton = new Button("Modifier");
        private final Button cancelButton = new Button("Annuler");

        public RendezVousCell() {
            gridPane.setHgap(10);
            gridPane.setVgap(5);
            gridPane.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));

            // Style des labels
            dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            heureLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            lieuLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

            // Style des boutons
            editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

            // Ajout des éléments à la grille
            gridPane.add(dateLabel, 0, 0);
            gridPane.add(heureLabel, 1, 0);
            gridPane.add(lieuLabel, 2, 0);
            gridPane.add(statusLabel, 3, 0);
            gridPane.add(editButton, 4, 0);
            gridPane.add(cancelButton, 5, 0);

            // Actions des boutons
            editButton.setOnAction(event -> {
                RendezVous rendezVous = getItem();
                if (rendezVous != null) {
                    modifyAppointment(rendezVous);
                }
            });

            cancelButton.setOnAction(event -> {
                RendezVous rendezVous = getItem();
                if (rendezVous != null) {
                    cancelAppointment(rendezVous);
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
                heureLabel.setText("⏰ " + rendezVous.getTimeRendezVous());
                lieuLabel.setText("📍 " + rendezVous.getLieu());
                statusLabel.setText("📌 Statut: " + rendezVous.getStatus());
                setGraphic(gridPane);
            }
        }
    }
}