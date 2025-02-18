package mdinteech.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mdinteech.controllers.LoginController;

import java.io.IOException;

public class SharedMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Afficher la page de connexion ou de sélection (admin/user)
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/LoginView.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setMainApp(this); // Appel correct de la méthode

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Connexion");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAdminInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/admin/admin_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Interface");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showUserInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/MainWindow.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("User Interface");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}