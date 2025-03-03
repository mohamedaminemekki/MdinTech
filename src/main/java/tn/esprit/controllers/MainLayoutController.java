package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainLayoutController {
    @FXML
    private BorderPane mainContainer;
    private UserController userController;

    public void initialize() {
        loadUserInterface();
    }

    private void loadUserInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/UserInterface.fxml"));
            Parent userInterface = loader.load();
            userController = loader.getController();
            mainContainer.setCenter(userInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // These public methods allow toggling between views
    public void showUnpaid() {
        if (userController != null) {
            userController.showUnpaid();
        }
    }

    public void showPaid() {
        if (userController != null) {
            userController.showPaid();
        }
    }

    // You can add additional methods for other views here
}
