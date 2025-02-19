package org.example.mdintech.Controller.parkingController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.entities.ParkingModule.ParkingTicket;
import org.example.mdintech.service.ParkingModule.ParkingTicketService;
import org.example.mdintech.utils.navigation;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DisplayParkingDetailsViewController implements Initializable {

    @FXML
    private Label parkingNameLabel;

    @FXML
    private Label parkingLocationLabel;

    @FXML
    private Label parkingCapacityLabel;

    @FXML
    private ListView<String> ticketListView;


    private ParkingTicketService parkingTicketService;
    private int parkingId;  // Set this from the previous screen when navigating

    public DisplayParkingDetailsViewController() {
        this.parkingTicketService = new ParkingTicketService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadParkingTickets();
    }


    public void setParking(Parking parking) {
        if (parking != null) {
            parkingNameLabel.setText("Name: " + parking.getName());
            parkingLocationLabel.setText("Location: " + parking.getLocalisation());
            parkingCapacityLabel.setText("Capacity: " + parking.getCapacity());
        }
    }
    private void loadParkingTickets() {
        List<ParkingTicket> tickets = parkingTicketService.findByParkingId(parkingId);
        ticketListView.getItems().clear();

        if (tickets.isEmpty()) {
            ticketListView.getItems().add("NO tickets for this parking");
        } else {
            for (ParkingTicket ticket : tickets) {
                String ticketDetails = "Ticket #" + ticket.getId() +
                        " | Slot: " + ticket.getParkingSlotID() +
                        " | User: " + ticket.getUserID() +
                        " | Issued: " + ticket.getIssuingDate() +
                        " | Expires: " + ticket.getExpirationDate();
                ticketListView.getItems().add(ticketDetails);
            }
        }
    }
    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
        loadParkingTickets(); // Refresh when setting the parking ID
    }


    public void handleBackButton(ActionEvent event) throws IOException {
        navigation.switchScene(event, "/org/example/mdintech/ParkingModule/display-parkings-view.fxml");
    }

}
