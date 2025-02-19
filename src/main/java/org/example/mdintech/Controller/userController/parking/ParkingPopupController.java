package org.example.mdintech.Controller.userController.parking;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.entities.ParkingModule.ParkingTicket;
import org.example.mdintech.service.ParkingModule.ParkingSlotService;
import org.example.mdintech.service.ParkingModule.ParkingTicketService;
import org.example.mdintech.Singleton.loggedInUser;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ParkingPopupController {

    @FXML
    private DatePicker expirationDatePicker;
    @FXML
    private Button confirmButton;
    @FXML
    private ComboBox<Integer> hourComboBox;
    @FXML
    private ComboBox<Integer> minuteComboBox;


    private Parking selectedParking;
    private final ParkingSlotService parkingSlotService = new ParkingSlotService();
    private final ParkingTicketService ticketService = new ParkingTicketService();

    public void initialize() {
        // Populate hours (0-23)
        for (int i = 0; i < 24; i++) {
            hourComboBox.getItems().add(i);
        }
        // Populate minutes (0, 15, 30, 45) for better UX
        for (int i = 0; i < 60; i += 15) {
            minuteComboBox.getItems().add(i);
        }
    }

    public void initData(Parking parking) {
        if (parking == null) {
            showAlert("Invalid parking selection!");
            ((Stage) confirmButton.getScene().getWindow()).close();
            return;
        }
        this.selectedParking = parking;
        checkSlotAvailability();
    }

    private void checkSlotAvailability() {
        int availableSlot = parkingSlotService.getSmallestAvailableSlot(selectedParking.getID());
        if (availableSlot == -1) {
            showAlert("This parking is full!");
            ((Stage) confirmButton.getScene().getWindow()).close();
        }
    }



    @FXML
    private void confirmReservation() {
        int availableSlot = parkingSlotService.getSmallestAvailableSlot(selectedParking.getID());
        if (availableSlot == -1) {
            showAlert("This parking is full!");
            return;
        }

        LocalDate selectedDate = expirationDatePicker.getValue();
        Integer selectedHour = hourComboBox.getValue();
        Integer selectedMinute = minuteComboBox.getValue();

        if (selectedDate == null || selectedHour == null || selectedMinute == null) {
            showAlert("Please select a valid date and time!");
            return;
        }

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute));

        if (selectedDateTime.isBefore(now.plusHours(1))) {
            showAlert("The expiration time must be at least one hour from now!");
            return;
        }

        ParkingTicket ticket = new ParkingTicket(
                loggedInUser.getInstance().getLoggedUser().getCIN(),
                selectedParking.getID(),
                availableSlot,
                new Date(System.currentTimeMillis()),
                Timestamp.valueOf(selectedDateTime),
                true
        );

        ticketService.save(ticket);
        showAlert("Parking ticket created successfully!");
        ((Stage) confirmButton.getScene().getWindow()).close();
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
