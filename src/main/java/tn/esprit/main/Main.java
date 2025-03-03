package tn.esprit.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.utils.MyDatabase;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/Login.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Authentification SONEDE");
        stage.show();
    }

    public static void main(String[] args) {
        // Initialisation de la connexion à la base de données
        MyDatabase db = MyDatabase.getInstance();

        // Lancer l'interface JavaFX
        launch(args);

        /*
        // Exemple d'utilisation des services (à déplacer dans les contrôleurs)
        try {
            // Création d'une facture de test
            FactureServices factureService = new FactureServices();

            Facture facture = new Facture(
                0,
                new Date(),
                new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000), // +30 jours
                250.0f,
                "Électricité",
                false,
                "12345678"
            );

            factureService.add(facture);
            System.out.println("Facture créée avec ID: " + facture.getId());

            // Création d'un reçu associé
            if(facture.getId() > 0) {
                RecuServices recuService = new RecuServices();
                Recu recu = new Recu(
                    0,
                    facture.getUserCIN(), // Utilisation du CIN comme référence
                    facture.getId(),
                    new Date(),
                    facture.getPrixFact()
                );
                recuService.add(recu);
                System.out.println("Reçu créé avec ID: " + recu.getId());
            }

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
        */
    }
}