package mdinteech.controllers;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private ListView<HBox> tripListView;

    @FXML
    private ComboBox<String> departureField, destinationField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField priceField;

    @FXML
    private ImageView sliderImage;

    @FXML
    private Button reserveButton;

    @FXML
    private Label infoLabel;

    @FXML
    private VBox infoPanel;

    private TripService tripService;
    private ReservationService reservationService;
    private ObservableList<Trip> originalTrips;

    private final String[] sliderImages = {"/images/bus2.jpg", "/images/book2.jpg", "/images/book.jpg", "/images/metro3.jpg", "/images/bus3.jpg", "/images/bus.jpg", "/images/train.jpg", "/images/metro.jpg"};
    private int sliderIndex = 0;

    private int currentUserId = 9; // ID de l'utilisateur connecté (à adapter selon votre système d'authentification)

    public MainController() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", "");
            tripService = new TripService(connection);
            reservationService = new ReservationService(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            List<Trip> trips = tripService.readList();
            originalTrips = FXCollections.observableArrayList(trips);

            departureField.setItems(FXCollections.observableArrayList(trips.stream().map(Trip::getDeparture).distinct().collect(Collectors.toList())));
            destinationField.setItems(FXCollections.observableArrayList(trips.stream().map(Trip::getDestination).distinct().collect(Collectors.toList())));

            refreshTripListView(trips);
            startImageSlider();
            animateReserveButton();

            tripListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                reserveButton.setDisable(newSelection == null);
                if (newSelection != null) {
                    showTripDetails(newSelection.getUserData());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshTripListView(List<Trip> trips) {
        tripListView.getItems().clear();
        for (Trip trip : trips) {
            HBox tripCard = createTripCard(trip);
            tripListView.getItems().add(tripCard);
        }
    }

    private HBox createTripCard(Trip trip) {
        Label departureLabel = new Label("Départ: " + trip.getDeparture());
        Label destinationLabel = new Label("Destination: " + trip.getDestination());
        Label transportLabel = new Label("Transport: " + trip.getTransportName());
        Label priceLabel = new Label("Prix: " + trip.getPrice() + " DT");
        Label departureTimeLabel = new Label("Heure Départ: " + trip.getDepartureTime());
        Label arrivalTimeLabel = new Label("Heure Arrivée: " + trip.getArrivalTime());

        VBox leftVBox = new VBox(departureLabel, destinationLabel, transportLabel);
        VBox rightVBox = new VBox(priceLabel, departureTimeLabel, arrivalTimeLabel);
        HBox tripCard = new HBox(leftVBox, rightVBox);

        tripCard.setUserData(trip);
        tripCard.setSpacing(20);
        tripCard.setStyle("-fx-padding: 15px; -fx-background-color: #FFFFFF; -fx-border-radius: 10px; -fx-border-color: #BDC3C7; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 2);");

        // Changer les couleurs de texte pour la sélection
        tripCard.setOnMouseClicked(event -> {
            tripListView.getItems().forEach(item -> item.setStyle("-fx-padding: 15px; -fx-background-color: #FFFFFF; -fx-border-radius: 10px; -fx-border-color: #BDC3C7; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 2);"));
            tripCard.setStyle("-fx-padding: 15px; -fx-background-color: #2C3E50; -fx-border-radius: 10px; -fx-border-color: #2C3E50; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 3);");
            departureLabel.setStyle("-fx-text-fill: #ECF0F1;");
            destinationLabel.setStyle("-fx-text-fill: #ECF0F1;");
            transportLabel.setStyle("-fx-text-fill: #ECF0F1;");
            priceLabel.setStyle("-fx-text-fill: #ECF0F1;");
            departureTimeLabel.setStyle("-fx-text-fill: #ECF0F1;");
            arrivalTimeLabel.setStyle("-fx-text-fill: #ECF0F1;");
        });

        return tripCard;
    }

    private void startImageSlider() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            sliderIndex = (sliderIndex + 1) % sliderImages.length;
            sliderImage.setImage(new Image(getClass().getResourceAsStream(sliderImages[sliderIndex])));
            startImageSlider();
        });
        pause.play();
    }

    private void animateReserveButton() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), reserveButton);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);

        reserveButton.setOnMouseEntered(event -> scaleTransition.play());
    }

    @FXML
    private void quickSearch() {
        String departure = departureField.getValue();
        String destination = destinationField.getValue();
        LocalDate date = dateField.getValue();
        String priceText = priceField.getText();

        Double maxPrice = null;
        if (priceText != null && !priceText.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Veuillez entrer un prix valide.");
                return;
            }
        }

        Double finalMaxPrice = maxPrice;
        List<Trip> filteredTrips = originalTrips.stream()
                .filter(trip -> (departure == null || trip.getDeparture().equalsIgnoreCase(departure)) &&
                        (destination == null || trip.getDestination().equalsIgnoreCase(destination)) &&
                        (date == null || (trip.getDate() != null && trip.getDate().equals(date))) && // Vérification de null
                        (finalMaxPrice == null || trip.getPrice() <= finalMaxPrice))
                .collect(Collectors.toList());

        refreshTripListView(filteredTrips);
    }
    @FXML
    public void handleReserve(ActionEvent actionEvent) {
        try {
            HBox selectedCard = tripListView.getSelectionModel().getSelectedItem();
            if (selectedCard != null) {
                Trip selectedTrip = (Trip) selectedCard.getUserData();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/ReservationDetails.fxml"));
                Parent root = loader.load();

                ReservationDetailsController controller = loader.getController();
                controller.setTripDetails(selectedTrip);

                Stage stage = new Stage();
                stage.setTitle("Détails de la Réservation");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                showAlert("Aucun trajet sélectionné", "Veuillez sélectionner un trajet avant de réserver.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void showReservations(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/ReservationList.fxml"));
            Parent root = loader.load();

            // Passer l'ID de l'utilisateur au contrôleur de la liste des réservations
            ReservationListController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Mes Réservations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la page des réservations.");
        }
    }
    private void showTripDetails(Object tripData) {
        Trip trip = (Trip) tripData;
        infoLabel.setText("Détails du trajet sélectionné:\n" +
                "Départ: " + trip.getDeparture() + "\n" +
                "Destination: " + trip.getDestination() + "\n" +
                "Transport: " + trip.getTransportName() + "\n" +
                "Prix: " + trip.getPrice() + " DT\n" +
                "Heure Départ: " + trip.getDepartureTime() + "\n" +
                "Heure Arrivée: " + trip.getArrivalTime());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goToHome(ActionEvent actionEvent) {
        refreshTripListView(originalTrips);
    }

    public void contactSupport(ActionEvent actionEvent) {
        showAlert("Support", "Contactez-nous à support@mdintech.com.");
    }

    public void handleProfile(ActionEvent actionEvent) {
        showAlert("Profil", "Gérez votre profil ici.");
    }

    @FXML
    private void clearSearch() {
        departureField.setValue(null); // Réinitialiser le champ de départ
        destinationField.setValue(null); // Réinitialiser le champ de destination
        dateField.setValue(null); // Réinitialiser le champ de date
        priceField.clear(); // Réinitialiser le champ de prix

        try {
            refreshTripListView(tripService.readList()); // Rafraîchir la liste des trajets
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement des trajets.");
        }
    }
    @FXML
    private void openTrainMap() {
        openMap("/mdinteech/views/train_map.fxml", "Carte des Trains");
    }

    @FXML
    private void openBusMap() {
        openMap("/mdinteech/views/bus_map.fxml", "Carte des Bus");
    }

    @FXML
    private void openMetroMap() {
        openMap("/mdinteech/views/metro_map.fxml", "Carte du Métro");
    }

    private void openMap(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}