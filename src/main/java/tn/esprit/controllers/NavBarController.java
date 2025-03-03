package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.controllers.UserController; // Controller for the factures content
import java.io.IOException;

public class NavBarController {

    @FXML
    private VBox navContainer;

    @FXML
    private void navigateToFactures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/UserInterface.fxml"));
            Parent facturesRoot = loader.load();
            UserController UserController = loader.getController();
            UserController.showUnpaid();
            BorderPane mainLayout = getMainLayout();
            if (mainLayout != null) {
                mainLayout.setCenter(facturesRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToHistorique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/UserInterface.fxml"));
            Parent facturesRoot = loader.load();
            UserController UserController = loader.getController();
            UserController.showPaid();
            BorderPane mainLayout = getMainLayout();
            if (mainLayout != null) {
                mainLayout.setCenter(facturesRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToBlog() {
        try {
            Parent blogRoot = FXMLLoader.load(getClass().getResource("/tn/esprit/gui/blog_posts.fxml"));
            BorderPane mainLayout = getMainLayout();
            if (mainLayout != null) {
                mainLayout.setCenter(blogRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/tn/esprit/gui/login.fxml"));
            Stage stage = (Stage) navContainer.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BorderPane getMainLayout() {
        if (navContainer != null && navContainer.getScene() != null) {
            Parent root = navContainer.getScene().getRoot();
            if (root instanceof BorderPane) {
                return (BorderPane) root;
            }
        }
        System.err.println("Main layout not found. Ensure your main FXML uses a BorderPane as the root.");
        return null;
    }
}
