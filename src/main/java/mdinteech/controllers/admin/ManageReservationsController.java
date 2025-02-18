package mdinteech.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageReservationsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox; // Nouveau : ComboBox pour les filtres

    @FXML
    private ListView<Reservation> reservationsListView;

    private ReservationService reservationService;

    @FXML
    public void initialize() {
        try {
            // Initialiser ReservationService avec une connexion à la base de données
            reservationService = new ReservationService(DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", ""));
            loadReservations(); // Charger les réservations au démarrage
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de se connecter à la base de données.");
        }

        // Personnaliser l'affichage des éléments dans la ListView
        reservationsListView.setCellFactory(new Callback<ListView<Reservation>, ListCell<Reservation>>() {
            @Override
            public ListCell<Reservation> call(ListView<Reservation> param) {
                return new ListCell<Reservation>() {
                    @Override
                    protected void updateItem(Reservation reservation, boolean empty) {
                        super.updateItem(reservation, empty);
                        if (empty || reservation == null) {
                            setText(null);
                            setStyle(""); // Réinitialiser le style
                        } else {
                            // Afficher les détails de la réservation dans une cellule personnalisée
                            setText(String.format("Réservation #%d : User ID %d, Trip ID %d, Statut: %s",
                                    reservation.getId(),
                                    reservation.getUserId(),
                                    reservation.getTripId(),
                                    reservation.getPaymentStatus()));
                            setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
                        }
                    }
                };
            }
        });

        // Initialiser les filtres
        filterComboBox.getItems().addAll("Tous", "User ID", "Trip ID", "Statut");
        filterComboBox.setValue("Tous"); // Valeur par défaut

        // Recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchReservations();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de rechercher les réservations.");
            }
        });
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationService.readList();
            reservationsListView.getItems().setAll(reservations);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les réservations.");
        }
    }

    @FXML
    private void searchReservations() throws SQLException {
        String keyword = searchField.getText();
        String filter = filterComboBox.getValue(); // Récupérer le filtre sélectionné

        List<Reservation> reservations;
        if (keyword.isEmpty()) {
            reservations = reservationService.readList(); // Si la recherche est vide, charger toutes les réservations
        } else {
            switch (filter) {
                case "User ID":
                    reservations = reservationService.searchByUserId(keyword);
                    break;
                case "Trip ID":
                    reservations = reservationService.searchByTripId(keyword);
                    break;
                case "Statut":
                    reservations = reservationService.searchByPaymentStatus(keyword);
                    break;
                default:
                    reservations = reservationService.searchReservations(keyword); // Recherche générale
                    break;
            }
        }
        reservationsListView.getItems().setAll(reservations);
    }

    @FXML
    private void clearSearch() {
        searchField.clear(); // Effacer le champ de recherche
        filterComboBox.setValue("Tous"); // Réinitialiser le filtre
        try {
            loadReservations(); // Recharger toutes les réservations
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de réinitialiser la recherche.");
        }
    }

    @FXML
    private void openAddReservationDialog() {
        try {
            // Charger le fichier FXML pour l'ajout de réservation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/add_reservation.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur associé
            AddReservationController controller = loader.getController();
            controller.setReservationService(reservationService); // Injecter ReservationService dans le contrôleur

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre que la fenêtre soit fermée

            // Recharger les réservations après l'ajout
            loadReservations();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout de réservation.");
        }
    }

    @FXML
    private void openEditReservationDialog() {
        Reservation selectedReservation = reservationsListView.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                // Charger le fichier FXML pour la modification de réservation
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/edit_reservation.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur associé
                EditReservationController controller = loader.getController();
                controller.setReservation(selectedReservation); // Passer la réservation sélectionnée
                controller.setReservationService(reservationService); // Injecter ReservationService dans le contrôleur

                // Afficher la fenêtre
                Stage stage = new Stage();
                stage.setTitle("Modifier une réservation");
                stage.setScene(new Scene(root));
                stage.showAndWait(); // Attendre que la fenêtre soit fermée

                // Recharger les réservations après la modification
                loadReservations();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification de réservation.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner une réservation.");
        }
    }

    @FXML
    private void deleteReservation() {
        Reservation selectedReservation = reservationsListView.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
            alert.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    reservationService.delete(selectedReservation.getId());
                    loadReservations(); // Recharger la liste des réservations après la suppression
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer la réservation.");
                }
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner une réservation.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}