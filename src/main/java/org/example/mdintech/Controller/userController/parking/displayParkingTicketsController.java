package org.example.mdintech.Controller.userController.parking;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.mdintech.service.ParkingModule.ParkingService;
import org.example.mdintech.service.ParkingModule.ParkingTicketService;
import org.example.mdintech.entities.ParkingModule.ParkingTicket;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.Singleton.loggedInUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class displayParkingTicketsController {
    @FXML private ListView<String> activeTicketsListView;
    @FXML private ListView<String> allTicketsListView;
    private List<ParkingTicket> activeTickets; // Store active tickets
    private List<ParkingTicket> allTickets;   // Store all tickets

    private ParkingTicketService ticketService;
    private ParkingService parkingService;

    public displayParkingTicketsController() {
        ticketService = new ParkingTicketService();
        parkingService = new ParkingService();
    }

    @FXML
    private void initialize() {
        if (loggedInUser.getInstance().getLoggedUser() == null) {
            System.out.println("No user is logged in.");
            return;
        }

        int userId = loggedInUser.getInstance().getLoggedUser().getCIN();

        List<ParkingTicket> tickets = ticketService.findByUserId(userId);

        activeTickets = tickets.stream()
                .filter(ParkingTicket::isStatus)
                .collect(Collectors.toList());

        List<String> activeTicketDetails = activeTickets.stream()
                .map(this::mapTicketToString)
                .collect(Collectors.toList());
        activeTicketsListView.getItems().setAll(activeTicketDetails);

        allTickets = tickets; // Store all tickets
        List<String> allTicketDetails = allTickets.stream()
                .map(this::mapTicketToString)
                .collect(Collectors.toList());
        allTicketsListView.getItems().setAll(allTicketDetails);
    }

    private String mapTicketToString(ParkingTicket ticket) {
        Parking parking = parkingService.findById(ticket.getParkingID());

        String parkingName = (parking != null) ? parking.getName() : "Unknown";

        return "Parking: " + parkingName +
                " | Issuing Date: " + ticket.getIssuingDate() +
                " | Expiring Date: " + ticket.getExpirationDate() +
                " | Slot ID: " + ticket.getParkingSlotID();
    }

    @FXML
    private void handleTicketClick(MouseEvent event) {
        int selectedIndex = activeTicketsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            ParkingTicket selectedTicket = activeTickets.get(selectedIndex);

            System.out.println("Selected Ticket ID: " + selectedTicket.getId());

            showUpdateExpiryDateDialog(selectedTicket);
        } else {
            System.out.println("No ticket selected.");
        }
    }


    private void showUpdateExpiryDateDialog(ParkingTicket ticket) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Expiration Date");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField expirationDateField = new TextField();
        expirationDateField.setPromptText("Enter new expiration date (yyyy-MM-dd)");

        dialog.getDialogPane().setContent(expirationDateField);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return expirationDateField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date newExpirationDate = dateFormat.parse(result);

                ticket.setExpirationDate(newExpirationDate);

                ticketService.update(ticket);

                initialize();
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        });
    }

}
