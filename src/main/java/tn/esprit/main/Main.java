package tn.esprit.main;


import tn.esprit.entities.Facture;
import tn.esprit.entities.Recu;
import tn.esprit.services.FactureServices;
import tn.esprit.services.RecuServices;
import tn.esprit.utils.MyDatabase;

import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {  // Stage is from javafx.stage
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/tn/esprit/gui/Facture.fxml")));
        primaryStage.setTitle("Gestion Factures/Reçus");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        // Database connection
        MyDatabase db1 = MyDatabase.getInstance();

        // Create a new invoice (Facture)
        int userId = 1;  // ID de l'utilisateur (par exemple, un utilisateur ayant id = 1)
        Facture facture = new Facture(0, new Date(), 250.0f, "Sonede", true, userId); // Utilisation d'un prix valide

        // Instantiate FactureServices to handle invoice operations
        FactureServices factureServices = new FactureServices();

        // Try to read the list of invoices
        try {
            System.out.println(factureServices.readList());
        } catch (SQLException e) {
            System.out.println("Error reading factures: " + e.getMessage());
        }

        // Optionally, add a new invoice
        try {
            // Add the facture and retrieve the auto-generated ID
            factureServices.add(facture);
            System.out.println("Facture added successfully!");

            // Retrieve the last inserted facture ID (assuming auto-generated)
            int factureId = facture.getId();  // Cette valeur devrait être mise à jour après l'ajout de la facture
            System.out.println("Facture ID: " + factureId);

            // After adding the facture, create and add a Recu (receipt)
            // For example, a payment of 250.0f on the same day
            if (factureId > 0) {  // Assurez-vous que l'ID de la facture est valide
                Recu recu = new Recu(0, userId, factureId, new Date(), 250.0f);
                RecuServices recuServices = new RecuServices();
                recuServices.addRecu(recu);
                System.out.println("Reçu added successfully!");
            } else {
                System.out.println("Invalid facture ID, unable to create recu.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding facture or recu: " + e.getMessage());
        }
    }


    }

