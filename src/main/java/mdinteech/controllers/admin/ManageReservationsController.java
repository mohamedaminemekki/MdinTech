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
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageReservationsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private ListView<Reservation> reservationsListView;

    private ReservationService reservationService;

    @FXML
    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();

            reservationService = new ReservationService(connection);
            loadReservations();
        } catch (Exception e) {
            e.printStackTrace();
        }


        reservationsListView.setCellFactory(new Callback<ListView<Reservation>, ListCell<Reservation>>() {
            @Override
            public ListCell<Reservation> call(ListView<Reservation> param) {
                return new ListCell<Reservation>() {
                    @Override
                    protected void updateItem(Reservation reservation, boolean empty) {
                        super.updateItem(reservation, empty);
                        if (empty || reservation == null) {
                            setText(null);
                            setStyle("");
                        } else {

                            setText(String.format("Réservation #%d : User ID %d, Trip ID %d, Statut: %s",
                                    reservation.getId(),
                                    reservation.getUserId(),
                                    reservation.getTripId(),
                                    reservation.getStatus()));


                            if (reservation.getStatus().equals("cancelled")) {
                                setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #ff0000;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                };
            }
        });


        filterComboBox.getItems().addAll("Tous", "User ID", "Trip ID", "Statut");
        filterComboBox.setValue("Tous");


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
        String filter = filterComboBox.getValue();
        List<Reservation> reservations;
        if (keyword.isEmpty()) {
            reservations = reservationService.readList();
        } else {
            switch (filter) {
                case "User ID":
                    reservations = reservationService.searchByUserId(keyword);
                    break;
                case "Trip ID":
                    reservations = reservationService.searchByTripId(keyword);
                    break;
                case "Statut":
                    reservations = reservationService.searchByStatus(keyword);
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/add_reservation.fxml"));
            Parent root = loader.load();


            AddReservationController controller = loader.getController();
            controller.setReservationService(reservationService); // Injecter ReservationService dans le contrôleur


            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre que la fenêtre soit fermée


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

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/edit_reservation.fxml"));
                Parent root = loader.load();


                EditReservationController controller = loader.getController();
                controller.setReservation(selectedReservation);
                controller.setReservationService(reservationService);


                Stage stage = new Stage();
                stage.setTitle("Modifier une réservation");
                stage.setScene(new Scene(root));
                stage.showAndWait();


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
                    loadReservations();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer la réservation.");
                }
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner une réservation.");
        }
    }

    @FXML
    private void cancelReservation() {
        Reservation selectedReservation = reservationsListView.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            if (selectedReservation.getStatus().equals("confirmed") || selectedReservation.getStatus().equals("pending")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmer l'annulation");
                alert.setHeaderText("Êtes-vous sûr de vouloir annuler cette réservation ?");
                alert.setContentText("Cette action est irréversible.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {

                        selectedReservation.setStatus("cancelled");
                        reservationService.update(selectedReservation);

                        loadReservations();

                        showAlert("Succès", "La réservation a été annulée.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible d'annuler la réservation.");
                    }
                }
            } else {
                showAlert("Avertissement", "Seules les réservations actives peuvent être annulées.");
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