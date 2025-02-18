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
import mdinteech.services.TripService;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageTripsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox; // Nouveau : ComboBox pour les filtres

    @FXML
    private ListView<Trip> tripsListView;

    private TripService tripService;

    @FXML
    public void initialize() {
        try {
            // Initialiser TripService avec une connexion à la base de données
            tripService = new TripService(DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", ""));
            loadTrips(); // Charger les trajets au démarrage
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de se connecter à la base de données.");
        }

        // Personnaliser l'affichage des éléments dans la ListView
        tripsListView.setCellFactory(new Callback<ListView<Trip>, ListCell<Trip>>() {
            @Override
            public ListCell<Trip> call(ListView<Trip> param) {
                return new ListCell<Trip>() {
                    @Override
                    protected void updateItem(Trip trip, boolean empty) {
                        super.updateItem(trip, empty);
                        if (empty || trip == null) {
                            setText(null);
                            setStyle(""); // Réinitialiser le style
                        } else {
                            // Afficher les détails du trajet dans une cellule personnalisée
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

        // Initialiser les filtres
        filterComboBox.getItems().addAll("Tous", "Départ", "Destination", "Transport ID");
        filterComboBox.setValue("Tous"); // Valeur par défaut

        // Recherche dynamique
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
        String filter = filterComboBox.getValue(); // Récupérer le filtre sélectionné

        List<Trip> trips;
        if (keyword.isEmpty()) {
            trips = tripService.readList(); // Si la recherche est vide, charger tous les trajets
        } else {
            switch (filter) {
                case "Départ":
                    trips = tripService.searchByDeparture(keyword);
                    break;
                case "Destination":
                    trips = tripService.searchByDestination(keyword);
                    break;
                case "Transport ID":
                    trips = tripService.searchByTransportId(keyword);
                    break;
                default:
                    trips = tripService.searchTrips(keyword); // Recherche générale
                    break;
            }
        }
        tripsListView.getItems().setAll(trips);
    }

    @FXML
    private void clearSearch() {
        searchField.clear(); // Effacer le champ de recherche
        filterComboBox.setValue("Tous"); // Réinitialiser le filtre
        try {
            loadTrips(); // Recharger tous les trajets
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de réinitialiser la recherche.");
        }
    }

    @FXML
    private void openAddTripDialog() {
        try {
            // Charger le fichier FXML pour l'ajout de trajet
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdintech/views/admin/add_trip.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur associé
            AddTripController controller = loader.getController();
            controller.setTripService(tripService); // Injecter TripService dans le contrôleur

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Ajouter un trajet");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre que la fenêtre soit fermée

            // Recharger les trajets après l'ajout
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
                // Charger le fichier FXML pour la modification de trajet
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdintech/views/admin/edit_trip.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur associé
                EditTripController controller = loader.getController();
                controller.setTrip(selectedTrip); // Passer le trajet sélectionné
                controller.setTripService(tripService); // Injecter TripService dans le contrôleur

                // Afficher la fenêtre
                Stage stage = new Stage();
                stage.setTitle("Modifier un trajet");
                stage.setScene(new Scene(root));
                stage.showAndWait(); // Attendre que la fenêtre soit fermée

                // Recharger les trajets après la modification
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
                    loadTrips(); // Recharger la liste des trajets après la suppression
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