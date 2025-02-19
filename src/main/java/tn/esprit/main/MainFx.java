package tn.esprit.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML pour la page d'affichage des réclamations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load(); // Correct method name is 'load()'

            // Création de la scène
            Scene scene = new Scene(root);
            primaryStage.setTitle("Liste des Réclamations"); // Correct title for displaying reclamations
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
