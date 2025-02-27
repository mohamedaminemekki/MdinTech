package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class TicketViewController {

    @FXML
    private Label departureLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label seatTypeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView tripIcon;

    private ReservationService reservationService;
    private TripService tripService;

    public TicketViewController() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            reservationService = new ReservationService(connection);
            tripService = new TripService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReservation(Reservation reservation) {
        try {
            // Récupérer les détails du trajet associé à la réservation
            Trip trip = tripService.getById(reservation.getTripId());

            // Afficher les détails dans l'interface
            departureLabel.setText("Départ : " + trip.getDeparture());
            destinationLabel.setText("Destination : " + trip.getDestination());
            dateLabel.setText("Date : " + trip.getDepartureTime().toLocalDateTime().toLocalDate());
            priceLabel.setText("Prix : " + trip.getPrice() + " €");
            seatTypeLabel.setText("Type de siège : " + reservation.getSeatType());
            statusLabel.setText("Statut : " + reservation.getStatus());

            // Charger une icône pour le trajet
            tripIcon.setImage(new Image(getClass().getResourceAsStream("/images/trip.png")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
    }

    public void setReservationId(int id) {

    }
}