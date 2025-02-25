package tn.esprit.market_3a33.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.market_3a33.entities.UserRole;
import tn.esprit.market_3a33.services.UserService;


import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Simulate a CIN (you can get this from a login screen or other input)
        int CIN = 87654321; // Replace with actual CIN from your application logic

        // Fetch user role using UserService
        UserService userService = new UserService();
        UserRole userRole = userService.getUserRole(CIN);

        // Load the appropriate FXML based on the user role
        Parent root;
        if (userRole == UserRole.Admin) {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/tn/esprit/market_3a33/mainAdmin_view.fxml")));
        } else {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/tn/esprit/market_3a33/main_view.fxml")));
        }

        primaryStage.setTitle("Market");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}