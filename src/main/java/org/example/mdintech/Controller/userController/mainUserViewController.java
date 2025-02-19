package org.example.mdintech.Controller.userController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import java.io.IOException;

public class mainUserViewController {

    @FXML private MenuBar menuBar;  // Add this line

    @FXML private MenuItem goToHomeView;
    @FXML private MenuItem goToParkings;
    @FXML private MenuItem goToParkingTickets;
    @FXML private MenuItem goToUpdateProfile;

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
        loadView("/org/example/mdintech/views/userViews/homeView.fxml");
    }

    @FXML
    private void goToParkings() {
        loadView("/org/example/mdintech/userModule/parking/display-parkings.fxml");
    }

    @FXML
    private void goToParkingTickets() {
        loadView("/org/example/mdintech/userModule/parking/display-tickets.fxml");
    }

    public void goToUpdatePorfile(ActionEvent actionEvent) {loadView("/org/example/mdintech/userModule/update-user-view.fxml");}
}