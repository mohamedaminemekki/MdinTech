package org.example.mdintech.Controller.parkingController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.example.mdintech.entities.ParkingModule.ParkingTicket;
import org.example.mdintech.service.ParkingModule.ParkingTicketService;
import org.example.mdintech.service.userService;
import org.example.mdintech.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class DisplayParkingticketsViewController {

    @FXML
    private ListView<String> ticketListView;

    private final ParkingTicketService ticketService = new ParkingTicketService();
    private final userService userService = new userService();

    @FXML
    public void initialize() {
        loadParkingTickets();
    }

    private void loadParkingTickets() {
        List<ParkingTicket> tickets = ticketService.findAll();
        List<String> ticketDescriptions = tickets.stream()
                .map(ticket -> {
                    User user = userService.findById(ticket.getUserID());
                    String userName = (user != null) ? user.getName() : "Unknown User";
                    return "Parking ID: " + ticket.getParkingID() +
                            " | Slot: " + ticket.getParkingSlotID() +
                            " | User: " + userName +
                            " | Date: " + ticket.getIssuingDate();
                })
                .collect(Collectors.toList());

        ticketListView.getItems().addAll(ticketDescriptions);

        ticketListView.setOnMouseClicked(this::handleTicketClick);
    }

    private void handleTicketClick(MouseEvent event) {
        String selectedItem = ticketListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = ticketListView.getSelectionModel().getSelectedIndex();
            ParkingTicket selectedTicket = ticketService.findAll().get(selectedIndex);
            showTicketDetails(selectedTicket);
        }
    }

    private void showTicketDetails(ParkingTicket ticket) {
        User user = userService.findById(ticket.getUserID());
        String userName = (user != null) ? user.getName() : "Unknown User";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Parking Ticket Details");
        alert.setHeaderText("Ticket ID: " + ticket.getId());
        alert.setContentText("Parking ID: " + ticket.getParkingID() +
                "\nSlot ID: " + ticket.getParkingSlotID() +
                "\nUser: " + userName +
                "\nIssuing Date: " + ticket.getIssuingDate() +
                "\nExpiration Date: " + ticket.getExpirationDate() +
                "\nStatus: " + (ticket.isStatus() ? "Active" : "Expired"));

        alert.showAndWait();
    }
}
