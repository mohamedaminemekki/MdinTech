package mdinteech;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/admin_dashboard.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Tableau de bord administrateur");
            primaryStage.setScene(new Scene(root, 400, 300)); // Ajuste la taille si nécessaire
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void openManageReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(AdminMain.class.getResource("/mdinteech/views/admin/manage_reservations.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gérer les réservations");
            stage.setScene(new Scene(root, 500, 400)); // Ajuste la taille si nécessaire
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
