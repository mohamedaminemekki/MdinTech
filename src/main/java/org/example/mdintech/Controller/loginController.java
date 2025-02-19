package org.example.mdintech.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.mdintech.Singleton.loggedInUser;
import org.example.mdintech.entities.User;
import org.example.mdintech.service.userService;
import org.example.mdintech.utils.UserRole;

import java.io.IOException;

public class loginController {

    @FXML
    private TextField emailField;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    userService us=new userService();

    @FXML
    public void gotoSignIn(ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mdintech/userModule/sign-in-view.fxml"));
            Parent signInRoot = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(signInRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void login(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        User user=us.login(email, password);
        if (user != null) {
            loggedInUser.initializeSession((user));
            if (user.getRole() == UserRole.ADMIN) {
                goToDashboard(event, "/org/example/mdintech/main-admin-view.fxml");
            } else if (user.getRole() == UserRole.USER) {
                goToDashboard(event, "/org/example/mdintech/main-user-view.fxml");
            }
        }else{
            showAlert("User Not Found ","no credentials are matching the ones you gave us !!!!! .????");
        }
    }

    private void goToDashboard(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardRoot = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
