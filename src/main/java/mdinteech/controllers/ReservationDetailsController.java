package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ReservationDetailsController {

    @FXML
    private Spinner<Integer> passengerSpinner;

    @FXML
    private ComboBox<String> seatTypeComboBox;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private RadioButton payNowRadio, payLaterRadio;

    @FXML
    private Button confirmButton;

    private double selectedTripPrice;
    private int selectedTransportId;
    private String selectedDeparture;
    private String selectedDestination;
    private String selectedTransportName;
    private Reservation reservation;
    private int selectedTripId; // Ajout de l'ID du voyage sélectionné
    private ReservationService reservationService;

    @FXML
    public void initialize() {
        passengerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        passengerSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalPrice());
        seatTypeComboBox.getItems().addAll("Standard", "Premium");
        seatTypeComboBox.setValue("Standard");
        seatTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalPrice());

        ToggleGroup paymentGroup = new ToggleGroup();
        payNowRadio.setToggleGroup(paymentGroup);
        payLaterRadio.setToggleGroup(paymentGroup);
        payNowRadio.setSelected(true);

        confirmButton.setOnAction(event -> confirmReservation());

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", "");
            reservationService = new ReservationService(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTripDetails(Trip selectedTrip) {
        if (selectedTrip != null) {
            this.selectedTripId = selectedTrip.getTripId(); // Stocker l'ID du voyage
            this.selectedTripPrice = selectedTrip.getPrice();
            this.selectedTransportId = selectedTrip.getTransportId();
            this.selectedDeparture = selectedTrip.getDeparture();
            this.selectedDestination = selectedTrip.getDestination();
            this.selectedTransportName = selectedTrip.getTransportName();
            updateTotalPrice();
        }
    }

    public void setPassengerSpinnerValue(int passengers) {
        passengerSpinner.getValueFactory().setValue(passengers);
    }

    public void setSeatTypeComboBoxValue(String seatType) {
        seatTypeComboBox.setValue(seatType);
    }

    private void updateTotalPrice() {
        int passengers = passengerSpinner.getValue();
        boolean isPremium = seatTypeComboBox.getValue().contains("Premium");

        double price = selectedTripPrice * passengers;
        if (isPremium) {
            price += 10 * passengers; // Supplément premium de 10 DT par passager
        }

        totalPriceLabel.setText(price + " DT");
    }

    @FXML
    private void confirmReservation() {
        confirmButton.setDisable(true); // Désactiver le bouton après le premier clic
        System.out.println("Tentative de création de réservation...");

        if (seatTypeComboBox.getValue() == null || passengerSpinner.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            confirmButton.setDisable(false); // Réactiver le bouton en cas d'erreur
            return;
        }

        if (selectedTripId == 0) { // Vérifier si un voyage est sélectionné
            showAlert("Erreur", "Veuillez sélectionner un voyage valide.");
            confirmButton.setDisable(false); // Réactiver le bouton en cas d'erreur
            return;
        }

        reservation = new Reservation();
        reservation.setUserId(9); // Exemple d'ID utilisateur
        reservation.setTripId(selectedTripId);  // Utiliser l'ID du voyage sélectionné
        reservation.setTransportId(selectedTransportId);  // TransportId reste inchangé
        reservation.setReservationTime(new Timestamp(System.currentTimeMillis()));
        reservation.setStatus("Pending");
        reservation.setSeatNumber(passengerSpinner.getValue());
        reservation.setSeatType(seatTypeComboBox.getValue());
        reservation.setPaymentStatus(payNowRadio.isSelected() ? "Pending" : "Reserved");

        // Rediriger vers la page récapitulative
        loadReservationSummary(reservation);
    }
    private void loadReservationSummary(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/ReservationSummary.fxml"));
            Parent root = loader.load();

            mdinteech.controllers.ReservationSummaryController controller = loader.getController();
            controller.setReservationDetails(
                    reservation,
                    selectedDeparture,
                    selectedDestination,
                    selectedTransportName,
                    totalPriceLabel.getText() // Transmettre le prix total
            );

            Stage stage = new Stage();
            stage.setTitle("Récapitulatif de la Réservation");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) confirmButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
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