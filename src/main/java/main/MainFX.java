package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controllers.LoginController;

public class MainFX extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        // Charger la vue de login
        showLoginView();
    }

    public void showLoginView() throws Exception {
        // Charger l'écran de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent loginRoot = loader.load();

        // Passer l'instance de MainFX au LoginController
        LoginController controller = loader.getController();
        controller.setMainApp(this);

        // Créer la scène pour l'écran de login
        Scene loginScene = new Scene(loginRoot, 600, 450);

        // Appliquer le CSS
        loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Définir le titre de la fenêtre
        primaryStage.setTitle("Connexion");
        primaryStage.setScene(loginScene); // Définir la scène de login
        primaryStage.show(); // Afficher la fenêtre de login
    }

    public void showServiceView() throws Exception {
        // Charger l'interface des services
        Parent serviceRoot = FXMLLoader.load(getClass().getResource("/service-view.fxml"));

        // Créer la scène pour l'interface des services
        Scene serviceScene = new Scene(serviceRoot, 600, 450);

        // Appliquer le CSS
        serviceScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Définir le titre de la fenêtre
        primaryStage.setTitle("Services Hospitaliers");
        primaryStage.setScene(serviceScene); // Définir la scène des services
        primaryStage.show(); // Afficher l'interface des services
    }

    public void showAdminView() throws Exception {
        // Charger l'interface d'administration
        Parent adminRoot = FXMLLoader.load(getClass().getResource("/admin.fxml"));

        // Créer la scène pour l'interface d'administration
        Scene adminScene = new Scene(adminRoot, 900, 600);

        // Appliquer le CSS
        adminScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Définir le titre de la fenêtre
        primaryStage.setTitle("Administration");
        primaryStage.setScene(adminScene); // Définir la scène d'administration
        primaryStage.show(); // Afficher l'interface d'administration
    }

    public static void main(String[] args) {
        launch(args);
    }
}
