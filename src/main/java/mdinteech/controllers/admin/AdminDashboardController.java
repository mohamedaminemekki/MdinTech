package mdinteech.controllers.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private void openManageTrips() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/manage_trips.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gérer les trajets");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openManageReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/manage_reservations.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gérer les réservations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAdminProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/admin_profile.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Profil Admin");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/statistics.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Statistiques");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        try {

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}