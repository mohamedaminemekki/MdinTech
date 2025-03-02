package org.example.mdintech.Controller.userController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.example.mdintech.Singleton.loggedInUser;

import java.io.IOException;

public class mainAdminViewController {
    @FXML
    private MenuBar menuBar;  // Add this line

    @FXML private MenuItem goToHomeView;
    @FXML private MenuItem goToParkings;
    @FXML private MenuItem goToParkingTickets;
    @FXML private MenuItem goToUsers;
    @FXML private MenuItem goToCreateParking;

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get stage from the menuBar's scene
            Stage stage = (Stage) menuBar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToHomeView() {
        loadView("/org/example/mdintech/main-admin-view.fxml");
    }

    @FXML
    private void goToParkings() {
        loadView("/org/example/mdintech/ParkingModule/display-parkings-view.fxml");
    }

    @FXML
    private void goToParkingTickets() {
        loadView("/org/example/mdintech/ParkingModule/display-parkingtickets-view.fxml");
    }

    public void goToUsers(ActionEvent actionEvent) {loadView("/org/example/mdintech/userModule/display-users-view.fxml");}

    public void goToCreateParking(ActionEvent actionEvent) {loadView("/org/example/mdintech/ParkingModule/create-parking-view.fxml");}

    public void logout(ActionEvent actionEvent) {
        loggedInUser.clearSession();
        loadView("/org/example/mdintech/userModule/login-view.fxml");
    }
}
