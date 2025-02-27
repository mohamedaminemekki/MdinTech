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
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageTripsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private ListView<Trip> tripsListView;

    private TripService tripService;

    @FXML
    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();

        tripService = new TripService(connection);
            loadTrips();
        } catch (Exception e) {
            e.printStackTrace();
        }


        tripsListView.setCellFactory(new Callback<ListView<Trip>, ListCell<Trip>>() {
            @Override
            public ListCell<Trip> call(ListView<Trip> param) {
                return new ListCell<Trip>() {
                    @Override
                    protected void updateItem(Trip trip, boolean empty) {
                        super.updateItem(trip, empty);
                        if (empty || trip == null) {
                            setText(null);
                            setStyle("");
                        } else {

                            setText(String.format("Trajet #%d : %s → %s (%s - %s)",
                                    trip.getTripId(),
                                    trip.getDeparture(),
                                    trip.getDestination(),
                                    trip.getDepartureTime(),
                                    trip.getArrivalTime()));
                            setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
                        }
                    }
                };
            }
        });


        filterComboBox.getItems().addAll("Tous", "Départ", "Destination" );
        filterComboBox.setValue("Tous"); // Valeur par défaut


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchTrips();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de rechercher les trajets.");
            }
        });
    }

    private void loadTrips() {
        try {
            List<Trip> trips = tripService.readList();
            tripsListView.getItems().setAll(trips);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les trajets.");
        }
    }

    @FXML
    private void searchTrips() throws SQLException {
        String keyword = searchField.getText();
        String filter = filterComboBox.getValue();

        List<Trip> trips;
        if (keyword.isEmpty()) {
            trips = tripService.readList();
        } else {
            switch (filter) {
                case "Départ":
                    trips = tripService.searchByDeparture(keyword);
                    break;
                case "Destination":
                    trips = tripService.searchByDestination(keyword);

                    break;
                default:
                    trips = tripService.searchTrips(keyword);
                    break;
            }
        }
        tripsListView.getItems().setAll(trips);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        filterComboBox.setValue("Tous");
        try {
            loadTrips();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de réinitialiser la recherche.");
        }
    }

    @FXML
    private void openAddTripDialog() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/add_trip.fxml"));
            Parent root = loader.load();


            AddTripController controller = loader.getController();
            controller.setTripService(tripService);


            Stage stage = new Stage();
            stage.setTitle("Ajouter un trajet");
            stage.setScene(new Scene(root));
            stage.showAndWait();


            loadTrips();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout de trajet.");
        }
    }

    @FXML
    private void openEditTripDialog() {
        Trip selectedTrip = tripsListView.getSelectionModel().getSelectedItem();
        if (selectedTrip != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/edit_trip.fxml"));
                Parent root = loader.load();


                EditTripController controller = loader.getController();
                controller.setTrip(selectedTrip);
                controller.setTripService(tripService);


                Stage stage = new Stage();
                stage.setTitle("Modifier un trajet");
                stage.setScene(new Scene(root));
                stage.showAndWait();


                loadTrips();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification de trajet.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un trajet.");
        }
    }

    @FXML
    private void deleteTrip() {
        Trip selectedTrip = tripsListView.getSelectionModel().getSelectedItem();
        if (selectedTrip != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce trajet ?");
            alert.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    tripService.delete(selectedTrip.getTripId());
                    loadTrips();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer le trajet.");
                }
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un trajet.");
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