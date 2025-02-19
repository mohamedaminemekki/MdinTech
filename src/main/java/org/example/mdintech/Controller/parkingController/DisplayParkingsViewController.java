package org.example.mdintech.Controller.parkingController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.service.ParkingModule.ParkingService;
import org.example.mdintech.utils.navigation;

import java.io.IOException;
import java.util.List;

public class DisplayParkingsViewController {

    @FXML
    private ListView<String> parkingListView;

    private ParkingService parkingService;
    private List<Parking> parkings;

    @FXML
    public void initialize() {
        parkingService = new ParkingService();

        // Load parking data
        parkings = parkingService.findAll();
        ObservableList<String> parkingNames = FXCollections.observableArrayList();

        for (Parking p : parkings) {
            parkingNames.add(p.getName() + " (Capacity: " + p.getCapacity() + ")");
        }

        parkingListView.setItems(parkingNames);
        parkingListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Handle click event
        parkingListView.setOnMouseClicked(event -> {
            int selectedIndex = parkingListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Parking selectedParking = parkings.get(selectedIndex);
                goToDetailsView(event, selectedParking);
            }
        });
    }

    private void goToDetailsView(MouseEvent event, Parking parking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mdintech/ParkingModule/display-parking-details-view.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the selected parking
            DisplayParkingDetailsViewController controller = loader.getController();
            if (controller != null) {
                controller.setParking(parking);
                controller.setParkingId(parking.getID());
            }

            // Change the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleBackButton(ActionEvent event) throws IOException {
        navigation.switchScene(event, "/org/example/mdintech/main-admin-view.fxml");
    }
}
