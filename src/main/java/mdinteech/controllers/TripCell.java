package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import mdinteech.entities.Trip;

import java.io.IOException;

public class TripCell extends ListCell<Trip> {
    @FXML
    private Label departureLabel;
    @FXML
    private Label destinationLabel;
    @FXML
    private Label transportLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label departureTimeLabel;
    @FXML
    private Label arrivalTimeLabel;
    @FXML
    private HBox cellRoot;

    private FXMLLoader loader;

    @Override
    protected void updateItem(Trip trip, boolean empty) {
        super.updateItem(trip, empty);

        if (empty || trip == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/mdinteech/views/TripCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Mettre à jour les labels avec les données du trajet
            departureLabel.setText("Départ: " + trip.getDeparture());
            destinationLabel.setText("Destination: " + trip.getDestination());
            transportLabel.setText("Transport: " + trip.getTransportName());
            priceLabel.setText("Prix: " + trip.getPrice() + " DT");
            departureTimeLabel.setText("Heure Départ: " + trip.getDepartureTime());
            arrivalTimeLabel.setText("Heure Arrivée: " + trip.getArrivalTime());

            setText(null);
            setGraphic(cellRoot);
        }
    }
}