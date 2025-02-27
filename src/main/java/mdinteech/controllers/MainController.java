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
import mdinteech.services.WeatherService;
import mdinteech.utils.DatabaseConnection;
import org.json.JSONObject;

import java.io.IOException;
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

    private int currentUserId = 9;

    public MainController() {
        try {
            tripService = new TripService(DatabaseConnection.getInstance().getConnection());
            reservationService = new ReservationService(DatabaseConnection.getInstance().getConnection());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            List<Trip> trips = tripService.readList();
            originalTrips = FXCollections.observableArrayList(trips);

            // Log pour vérifier les données chargées
            System.out.println("Nombre de trajets chargés : " + trips.size());
            for (Trip trip : trips) {
                System.out.println("Départ : " + trip.getDeparture() + ", Destination : " + trip.getDestination());
            }

            departureField.setItems(FXCollections.observableArrayList(trips.stream().map(Trip::getDeparture).distinct().collect(Collectors.toList())));
            destinationField.setItems(FXCollections.observableArrayList(trips.stream().map(Trip::getDestination).distinct().collect(Collectors.toList())));

            tripListView.setCellFactory(listView -> new TripCell());

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
            tripCard.setUserData(trip); // Associer le Trip à la HBox
            tripListView.getItems().add(tripCard);
        }

    }

    private HBox createTripCard(Trip trip) {
        HBox tripCard = new HBox();
        tripCard.setSpacing(10);
        tripCard.setStyle("-fx-padding: 10px; -fx-background-color: #193b59; -fx-border-radius: 10px; -fx-border-color: #a0acb6; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 2);");

        // Ajouter des labels pour afficher les informations du voyage
        Label departureLabel = new Label("Départ : " + trip.getDeparture());
        departureLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label destinationLabel = new Label("Destination : " + trip.getDestination());
        destinationLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label priceLabel = new Label("Prix : " + trip.getPrice() + " DT");
        priceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        // Ajouter les labels à la HBox
        tripCard.getChildren().addAll(departureLabel, destinationLabel, priceLabel);

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
                        (date == null || (trip.getDate() != null && trip.getDate().equals(date))) &&
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

    @FXML
    private void showWeatherWindow(ActionEvent event) {
        HBox selectedCard = tripListView.getSelectionModel().getSelectedItem();
        if (selectedCard != null) {
            Trip selectedTrip = (Trip) selectedCard.getUserData();

            // Récupérer les données météo pour les villes de départ et de destination
            JSONObject departureData = WeatherService.getWeatherData(selectedTrip.getDeparture());
            JSONObject destinationData = WeatherService.getWeatherData(selectedTrip.getDestination());

            if (departureData != null && destinationData != null) {
                // Données pour la ville de départ
                String departureWeather = departureData.getJSONArray("weather").getJSONObject(0).getString("description");
                double departureTemp = departureData.getJSONObject("main").getDouble("temp") - 273.15; // Conversion de Kelvin à Celsius
                double departureHumidity = departureData.getJSONObject("main").getDouble("humidity");
                double departureWindSpeed = departureData.getJSONObject("wind").getDouble("speed");
                double departurePressure = departureData.getJSONObject("main").getDouble("pressure");
                String departureIconUrl = WeatherService.getWeatherIconUrl(departureData.getJSONArray("weather").getJSONObject(0).getString("icon"));

                // Données pour la ville de destination
                String destinationWeather = destinationData.getJSONArray("weather").getJSONObject(0).getString("description");
                double destinationTemp = destinationData.getJSONObject("main").getDouble("temp") - 273.15; // Conversion de Kelvin à Celsius
                double destinationHumidity = destinationData.getJSONObject("main").getDouble("humidity");
                double destinationWindSpeed = destinationData.getJSONObject("wind").getDouble("speed");
                double destinationPressure = destinationData.getJSONObject("main").getDouble("pressure");
                String destinationIconUrl = WeatherService.getWeatherIconUrl(destinationData.getJSONArray("weather").getJSONObject(0).getString("icon"));

                // Coordonnées géographiques des villes (exemple pour Tunis et Sousse)
                double departureLat = 36.8; // Latitude de Tunis
                double departureLon = 10.18; // Longitude de Tunis
                double destinationLat = 35.8; // Latitude de Sousse
                double destinationLon = 10.6; // Longitude de Sousse

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/WeatherView.fxml"));
                    Parent root = loader.load();

                    WeatherController controller = loader.getController();
                    controller.setWeather(
                            selectedTrip.getDeparture(), departureWeather, departureIconUrl,
                            departureData.getJSONObject("main").getDouble("temp"), // Température en Kelvin
                            departureHumidity, departureWindSpeed, departurePressure,
                            selectedTrip.getDestination(), destinationWeather, destinationIconUrl,
                            destinationData.getJSONObject("main").getDouble("temp"), // Température en Kelvin
                            destinationHumidity, destinationWindSpeed, destinationPressure
                    );

                    Stage stage = new Stage();
                    stage.setTitle("Météo pour votre trajet");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Erreur", "Impossible de récupérer les données météo pour une ou plusieurs villes.");
            }
        } else {
            showAlert("Aucun trajet sélectionné", "Veuillez sélectionner un trajet avant de voir la météo.");
        }
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
        departureField.setValue(null);
        destinationField.setValue(null);
        dateField.setValue(null);
        priceField.clear();

        try {
            refreshTripListView(tripService.readList());
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

    private void showWeather(String cityName) {
        String weatherInfo = String.valueOf(WeatherService.getWeatherData(cityName));
        infoLabel.setText(infoLabel.getText() + "\n\n" + weatherInfo);
    }

}