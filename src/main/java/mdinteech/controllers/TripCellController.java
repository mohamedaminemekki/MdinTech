package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import mdinteech.entities.Trip;

public class TripCellController {

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
    private ImageView transportIcon;
    @FXML
    private HBox cellRoot;

    public void setTrip(Trip trip) {
        // Mettre à jour les labels avec les données du trajet
        departureLabel.setText("Départ: " + trip.getDeparture());
        destinationLabel.setText("Destination: " + trip.getDestination());
        transportLabel.setText("Transport: " + trip.getTransportName());
        priceLabel.setText("Prix: " + trip.getPrice() + " DT");
        departureTimeLabel.setText("Heure Départ: " + trip.getDepartureTime());
        arrivalTimeLabel.setText("Heure Arrivée: " + trip.getArrivalTime());

        // Mettre à jour l'icône de transport
        switch (trip.getTransportName().toLowerCase()) {
            case "bus":
                transportIcon.setImage(new Image(getClass().getResourceAsStream("/images/busicon.png")));
                break;
            case "train":
                transportIcon.setImage(new Image(getClass().getResourceAsStream("/images/trainicon.png")));
                break;
            case "metro":
                transportIcon.setImage(new Image(getClass().getResourceAsStream("/images/metroicon.png")));
                break;
            default:
                transportIcon.setImage(new Image(getClass().getResourceAsStream("/images/trainicon.png")));
        }
    }

    public HBox getCellRoot() {
        return cellRoot;
    }

    // Méthode pour appliquer un style lors de la sélection
    public void setSelected(boolean selected) {
        if (selected) {
            cellRoot.setStyle("-fx-background-color: #2C3E50; -fx-border-radius: 10px; -fx-border-color: #2C3E50;");
            departureLabel.setStyle("-fx-text-fill: white;");
            destinationLabel.setStyle("-fx-text-fill: white;");
            transportLabel.setStyle("-fx-text-fill: white;");
            priceLabel.setStyle("-fx-text-fill: white;");
            departureTimeLabel.setStyle("-fx-text-fill: white;");
            arrivalTimeLabel.setStyle("-fx-text-fill: white;");
        } else {
            cellRoot.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10px; -fx-border-color: #BDC3C7;");
            departureLabel.setStyle("-fx-text-fill: black;");
            destinationLabel.setStyle("-fx-text-fill: black;");
            transportLabel.setStyle("-fx-text-fill: black;");
            priceLabel.setStyle("-fx-text-fill: black;");
            departureTimeLabel.setStyle("-fx-text-fill: black;");
            arrivalTimeLabel.setStyle("-fx-text-fill: black;");
        }
    }
}