package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ReservationListController {

    @FXML
    private VBox reservationContainer;

    private ReservationService reservationService;
    private TripService tripService;
    private int currentUserId = 9; // ID de l'utilisateur connecté (à adapter selon votre système d'authentification)

    public ReservationListController() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", "");
            reservationService = new ReservationService(connection);
            tripService = new TripService(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Charger les réservations depuis la base de données
        loadReservations();
    }

    private void loadReservations() {
        List<Reservation> reservations = reservationService.getReservationsByUserId(currentUserId);
        for (Reservation reservation : reservations) {
            // Créer une carte pour chaque réservation
            AnchorPane card = createReservationCard(reservation);
            reservationContainer.getChildren().add(card);
        }
    }

    private AnchorPane createReservationCard(Reservation reservation) {
        // Créer une carte pour la réservation
        AnchorPane card = new AnchorPane();
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5px; -fx-padding: 10px;");

        // Ajouter les détails de la réservation
        Label tripLabel = new Label("Trajet : " + reservation.getTripId());
        tripLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        tripLabel.setLayoutX(10);
        tripLabel.setLayoutY(10);

        Label passengersLabel = new Label("Nombre de passagers : " + reservation.getSeatNumber());
        passengersLabel.setLayoutX(10);
        passengersLabel.setLayoutY(40);

        Label seatTypeLabel = new Label("Type de siège : " + reservation.getSeatType());
        seatTypeLabel.setLayoutX(10);
        seatTypeLabel.setLayoutY(70);

        Label statusLabel = new Label("Statut : " + reservation.getStatus());
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(100);

        // Ajouter les boutons "Modifier" et "Supprimer"
        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        modifyButton.setLayoutX(250);
        modifyButton.setLayoutY(10);
        modifyButton.setOnAction(event -> modifyReservation(reservation));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setLayoutX(250);
        deleteButton.setLayoutY(50);
        deleteButton.setOnAction(event -> deleteReservation(reservation));

        // Ajouter les éléments à la carte
        card.getChildren().addAll(tripLabel, passengersLabel, seatTypeLabel, statusLabel, modifyButton, deleteButton);

        return card;
    }

    private void modifyReservation(Reservation reservation) {
        try {
            // Charger la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/ModifyReservation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la fenêtre de modification
            ModifyReservationController modifyController = loader.getController();

            // Passer la réservation sélectionnée au contrôleur de modification
            modifyController.setReservation(reservation);

            // Créer une nouvelle scène et afficher la fenêtre de modification
            Stage stage = new Stage();
            stage.setTitle("Modifier la Réservation");
            stage.setScene(new Scene(root));

            // Rafraîchir la liste des réservations après la fermeture de la fenêtre de modification
            stage.setOnHidden(event -> {
                reservationContainer.getChildren().clear();
                loadReservations();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la fenêtre de modification.");
        }
    }

    private void deleteReservation(Reservation reservation) {
        try {
            // Supprimer la réservation de la base de données
            reservationService.delete(reservation.getId());

            // Recharger les réservations
            reservationContainer.getChildren().clear();
            loadReservations();

            showAlert("Succès", "La réservation a été supprimée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer la réservation.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}