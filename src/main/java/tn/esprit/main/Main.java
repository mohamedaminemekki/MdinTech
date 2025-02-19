package tn.esprit.main;

import tn.esprit.entities.Reclamation;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ReclamationServices;
import tn.esprit.services.ReponseServices;
import tn.esprit.utils.MyDataBase;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        MyDataBase db = MyDataBase.getInstance();
        ReclamationServices reclamationService = new ReclamationServices();
        ReponseServices reponseService = new ReponseServices();

        // Lire la liste des réclamations
        try {
            System.out.println("Liste des Réclamations :");
            List<Reclamation> reclamations = reclamationService.readList();
            for (Reclamation r : reclamations) {
                System.out.println(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la lecture des réclamations : " + e.getMessage());
        }

        // Lire les réponses
        try {
            System.out.println("\nListe des Réponses :");
            List<Reponse> reponses = reponseService.readList();
            for (Reponse rep : reponses) {
                System.out.println(rep);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la lecture des réponses : " + e.getMessage());
        }

        // Ajouter une réclamation
        try {
            Reclamation r = new Reclamation(2, "2025-02-07", "L'application présente plusieurs bugs", true, "Problème application", ".jpg");
            reclamationService.add(r);
            System.out.println("\nRéclamation ajoutée avec succès : " + r);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réclamation : " + e.getMessage());
        }
/*
        // Mise à jour d'une réclamation
        try {
            int id = 3;
            Reclamation updatedReclamation = new Reclamation();
            updatedReclamation.setId(id);
            updatedReclamation.setClient_id(2);
            updatedReclamation.setDatee("2025-02-07");
            updatedReclamation.setDescription("Mise à jour : Le problème a été résolu.");
            updatedReclamation.setState(false);
            updatedReclamation.setType("Problème résolu");
            updatedReclamation.setPhoto("resolved_issue.jpg");

            reclamationService.update(updatedReclamation);
            System.out.println("\nRéclamation mise à jour avec succès : " + updatedReclamation);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la réclamation : " + e.getMessage());
        }

        // Suppression d'une réclamation
        try {
            int reclamationIdToDelete = 1;
            reclamationService.delete(reclamationIdToDelete);
            System.out.println("\nRéclamation supprimée avec succès (ID: " + reclamationIdToDelete + ")");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réclamation : " + e.getMessage());
        }

        // Ajouter une réponse
        try {
            Reponse response = new Reponse(2, "Votre problème a été résolu !", "2025-02-08");
            reponseService.add(response);
            System.out.println("\nRéponse ajoutée avec succès : " + response);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réponse : " + e.getMessage());
        }

        // Mise à jour d'une réponse
        try {
            Reponse updatedResponse = new Reponse(2, 2, "Message de réponse mis à jour", "2025-02-09");
            reponseService.update(updatedResponse);
            System.out.println("\nRéponse mise à jour avec succès : " + updatedResponse);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la réponse : " + e.getMessage());
        }

        // Suppression d'une réponse
        try {
            int idToDelete = 2;
            reponseService.delete(idToDelete);
            System.out.println("\nRéponse supprimée avec succès (ID: " + idToDelete + ")");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réponse : " + e.getMessage());
        }

 */
    }
}
