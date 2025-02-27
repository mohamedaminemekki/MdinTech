package org.example.mdintech.Controller.userController.parking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.service.ParkingModule.ParkingService;
import org.example.mdintech.service.ParkingModule.ParkingSlotService;
import org.example.mdintech.utils.navigation;

import java.io.IOException;
import java.util.List;

public class displayparkingsController {

    @FXML
    private ListView<String> parkingListView;

    private final ParkingService parkingService = new ParkingService();
    private final ParkingSlotService parkingSlotService = new ParkingSlotService();

    @FXML
    public void initialize() {
        loadParkings();
        parkingListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Handle parking selection
        parkingListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedItem = parkingListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    openParkingPopup(selectedItem); // Pass the full list view item
                }
            }
        });
    }

    private void loadParkings() {
        List<Parking> parkings = parkingService.findAll();
        for (Parking parking : parkings) {
            parkingListView.getItems().add(parking.getName() + " - " + parking.getLocalisation());
        }
    }

    private void openParkingPopup(String listViewItem) {
        try {
            // Split the list view item to extract the parking name
            String parkingName = listViewItem.split(" - ")[0]; // Split on " - " and take first part
            Parking parking = parkingService.findByName(parkingName);

            if (parking == null) {
                showAlert("Error: Parking not found!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mdintech/userModule/parking/ParkingPopup.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Parking Details");

            ParkingPopupController controller = loader.getController();
            controller.initData(parking); // Pass the valid parking object

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public void handleBackButton(ActionEvent event) throws IOException {
        navigation.switchScene(event, "/org/example/mdintech/main-user-view.fxml");
    }
}
