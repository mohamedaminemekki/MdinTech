package org.example.mdintech.Controller.userController.parking;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.entities.ParkingModule.ParkingTicket;
import org.example.mdintech.service.ParkingModule.ParkingSlotService;
import org.example.mdintech.service.ParkingModule.ParkingTicketService;
import org.example.mdintech.Singleton.loggedInUser;

import java.sql.Date;
import java.time.LocalDate;

public class ParkingPopupController {

    @FXML
    private DatePicker expirationDatePicker;
    @FXML
    private Button confirmButton;

    private Parking selectedParking;
    private final ParkingSlotService parkingSlotService = new ParkingSlotService();
    private final ParkingTicketService ticketService = new ParkingTicketService();

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

        LocalDate expirationDate = expirationDatePicker.getValue();
        if (expirationDate == null) {
            showAlert("Please select an expiration date!");
            return;
        }

        ParkingTicket ticket = new ParkingTicket(
                loggedInUser.getInstance().getLoggedUser().getCIN(),
                selectedParking.getID(),
                availableSlot,
                new Date(System.currentTimeMillis()), // Issuing date
                Date.valueOf(expirationDate), // Expiration date
                true // Active state
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
