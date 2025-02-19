package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ReservationListController {

    @FXML
    private VBox reservationContainer;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    private ReservationService reservationService;
    private TripService tripService;
    private int currentUserId = 9;

    public ReservationListController() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            tripService = new TripService(connection);
            reservationService = new ReservationService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {

        filterComboBox.getItems().addAll("Toutes", "Confirmed", "Pending", "Cancelled");
        filterComboBox.setValue("Toutes");


        loadReservations();


        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterReservations());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterReservations());
    }

    private void loadReservations() {
        List<Reservation> reservations = reservationService.getReservationsByUserId(currentUserId);
        displayReservations(reservations);
    }

    private void displayReservations(List<Reservation> reservations) {
        reservationContainer.getChildren().clear();
        for (Reservation reservation : reservations) {
            AnchorPane card = createReservationCard(reservation);
            reservationContainer.getChildren().add(card);
        }
    }

    private AnchorPane createReservationCard(Reservation reservation) {
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5px; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setLayoutX(10);
        grid.setLayoutY(10);


        ImageView tripIcon = loadIcon("/images/trip.png", 20, 20);
        if (tripIcon != null) {
            grid.add(tripIcon, 0, 0);
        }


        Label tripLabel = new Label("Trajet : " + reservation.getTripId());
        tripLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        tripLabel.setTextFill(Color.DARKBLUE);
        grid.add(tripLabel, 1, 0);


        Label passengersLabel = new Label("Passagers : " + reservation.getSeatNumber());
        passengersLabel.setFont(Font.font("Arial", 12));
        grid.add(passengersLabel, 1, 1);


        Label seatTypeLabel = new Label("Siège : " + reservation.getSeatType());
        seatTypeLabel.setFont(Font.font("Arial", 12));
        grid.add(seatTypeLabel, 1, 2);


        Label statusLabel = new Label("Statut : " + reservation.getStatus());
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        grid.add(statusLabel, 1, 3);


        if (reservation.getStatus().equals("Cancelled")) {
            card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5px; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            statusLabel.setTextFill(Color.RED);
        } else if (reservation.getStatus().equals("Pending")) {
            statusLabel.setTextFill(Color.ORANGE);
        } else {
            statusLabel.setTextFill(Color.GREEN);
        }


        HBox buttonBox = new HBox(10);
        buttonBox.setLayoutX(300);
        buttonBox.setLayoutY(10);


        Button detailsButton = new Button("Détails", loadIcon("/images/details.png", 16, 16));
        detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
        detailsButton.setOnAction(event -> showReservationDetails(reservation));


        if (reservation.getStatus().equals("Confirmed") || reservation.getStatus().equals("Pending")) {
            Button modifyButton = new Button("Modifier", loadIcon("/images/edit.png", 16, 16));
            modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
            modifyButton.setOnAction(event -> modifyReservation(reservation));
            buttonBox.getChildren().add(modifyButton);
        }


        if (reservation.getStatus().equals("Confirmed") || reservation.getStatus().equals("Pending")) {
            Button cancelButton = new Button("Annuler", loadIcon("/images/cancel.png", 16, 16));
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
            cancelButton.setOnAction(event -> cancelReservation(reservation));
            buttonBox.getChildren().add(cancelButton);
        }


        card.getChildren().addAll(grid, buttonBox);
        return card;
    }

    private ImageView loadIcon(String path, double width, double height) {
        try {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(path)));
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return imageView;
        } catch (Exception e) {
            System.err.println("Icône non trouvée : " + path);
            return null;
        }
    }

    private void showReservationDetails(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la réservation");
        alert.setHeaderText("Détails pour le trajet : " + reservation.getTripId());
        alert.setContentText(
                "Nombre de passagers : " + reservation.getSeatNumber() + "\n" +
                        "Type de siège : " + reservation.getSeatType() + "\n" +
                        "Statut : " + reservation.getStatus()
        );
        alert.showAndWait();
    }

    private void modifyReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/ModifyReservation.fxml"));
            Parent root = loader.load();

            ModifyReservationController modifyController = loader.getController();
            modifyController.setReservation(reservation);

            Stage stage = new Stage();
            stage.setTitle("Modifier la Réservation");
            stage.setScene(new Scene(root));

            stage.setOnHidden(event -> loadReservations());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la fenêtre de modification.");
        }
    }

    private void cancelReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer l'annulation");
        alert.setHeaderText("Êtes-vous sûr de vouloir annuler cette réservation ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatus("Cancelled");
                reservationService.update(reservation);
                loadReservations();
                showAlert("Succès", "La réservation a été annulée.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'annuler la réservation.");
            }
        }
    }

    private void filterReservations() {
        String searchText = searchField.getText().toLowerCase();
        String filter = filterComboBox.getValue();

        List<Reservation> reservations = reservationService.getReservationsByUserId(currentUserId);
        List<Reservation> filteredReservations = reservations.stream()
                .filter(reservation -> (filter.equals("Toutes") || reservation.getStatus().equals(filter)))
                .filter(reservation ->
                        String.valueOf(reservation.getTripId()).toLowerCase().contains(searchText) ||
                                String.valueOf(reservation.getSeatNumber()).contains(searchText) ||
                                reservation.getSeatType().toLowerCase().contains(searchText) ||
                                reservation.getStatus().toLowerCase().contains(searchText)
                )
                .toList();

        displayReservations(filteredReservations);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}