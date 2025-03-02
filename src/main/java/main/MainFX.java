package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import controllers.LoginController;

public class MainFX extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        showLoginView();
    }

    public void showLoginView() throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent loginRoot = loader.load();


        LoginController controller = loader.getController();
        controller.setMainApp(this);


        Scene loginScene = new Scene(loginRoot, 800, 600);


        loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());


        primaryStage.setTitle("Connexion");
        primaryStage.setScene(loginScene); // Définir la scène de login
        primaryStage.show(); // Afficher la fenêtre de login
    }

    public void showServiceView() throws Exception {

        Parent serviceRoot = FXMLLoader.load(getClass().getResource("/service-view.fxml"));


        Scene serviceScene = new Scene(serviceRoot, 1200, 700);



        serviceScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());


        primaryStage.setTitle("Services Hospitaliers");
        primaryStage.setScene(serviceScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void showAdminView() throws Exception {

        Parent adminRoot = FXMLLoader.load(getClass().getResource("/admin.fxml"));


        Scene adminScene = new Scene(adminRoot, 1200, 700);

        // Appliquer le CSS
        adminScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());


        primaryStage.setTitle("Administration");
        primaryStage.setScene(adminScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
