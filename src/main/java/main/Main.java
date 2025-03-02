package main;

import entities.Medecin;
import entities.RendezVous;
import entities.ServiceHospitalier;
import services.MedecinServices;
import services.RendezVousServices;
import services.ServiceHospitalierServices;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("====== Test CRUD ServiceHopitalier ======");
        //testServiceHopitalierCRUD();

        System.out.println("\n====== Test CRUD Medecin ======");
        //testMedecinCRUD();

        System.out.println("\n====== Test CRUD RendezVous ======");
        testRendezVousCRUD();
    }



    // Méthode pour tester les CRUD de RendezVous
    private static void testRendezVousCRUD() {
        RendezVousServices rdvServices = new RendezVousServices();

        try {
            // Ajouter un rendez-vous
            LocalDate date = LocalDate.parse("2025-02-19", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime time = LocalTime.parse("15:00:00", DateTimeFormatter.ofPattern("HH:mm:ss"));
            rdvServices.add(new RendezVous(date, time, "Salle B", "confirmé", 4)); // ID du médecin est passé ici
            System.out.println("Rendez-vous ajouté avec succès.");

            // Lire et afficher la liste des rendez-vous
            List<RendezVous> rendezVousList = rdvServices.readList();
            System.out.println("Liste des rendez-vous :");
            for (RendezVous rdv : rendezVousList) {
                System.out.println(rdv);
            }

            // Mettre à jour un rendez-vous existant
            LocalDate newDate = LocalDate.parse("2025-07-12", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime newTime = LocalTime.parse("11:00:00", DateTimeFormatter.ofPattern("HH:mm:ss"));
            rdvServices.update(new RendezVous(21, newDate, newTime, "Salle C", "confirmé", 5)); // Mise à jour avec l'ID du médecin
            System.out.println("Rendez-vous mis à jour avec succès.");

            // Supprimer un rendez-vous
            rdvServices.delete(25); // ID du rendez-vous à supprimer
            System.out.println("Rendez-vous supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }





}
