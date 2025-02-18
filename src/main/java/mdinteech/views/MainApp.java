package mdinteech.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mdinteech.utils.SceneManager;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlPath = getClass().getResource("/mdinteech/views/MainWindow.fxml");
            if (fxmlPath == null) {
                System.err.println("Erreur : Impossible de charger le fichier FXML !");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlPath);
            BorderPane root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Smart City Application");

            // Initialisation du SceneManager
            SceneManager.setPrimaryStage(primaryStage);

            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
