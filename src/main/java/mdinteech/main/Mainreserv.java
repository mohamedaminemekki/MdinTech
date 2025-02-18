package mdinteech.main;

import mdinteech.entities.Reservation;
import mdinteech.services.ReservationService;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Mainreserv {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Connexion à la base de données
        String url = "jdbc:mysql://localhost:3306/city_transport"; // Modifiez avec votre URL de base de données
        String user = "root";  // Modifiez avec votre utilisateur
        String password = "";  // Modifiez avec votre mot de passe

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            ReservationService reservationService = new ReservationService(connection);

            while (true) {
                System.out.println("\n=== Menu Gestion des Réservations ===");
                System.out.println("1. Lire toutes les réservations");
                System.out.println("2. Ajouter une réservation");
                System.out.println("3. Mettre à jour une réservation");
                System.out.println("4. Supprimer une réservation");
                System.out.println("5. Quitter");
                System.out.print("Votre choix : ");

                int choice;
                try {
                    choice = scanner.nextInt();
                } catch (Exception e) {
                    System.out.println("Veuillez entrer un nombre valide.");
                    scanner.nextLine(); // Nettoyer le buffer
                    continue;
                }

                switch (choice) {
                    case 1:
                        // Lire toutes les réservations
                        List<Reservation> reservations = reservationService.readList();
                        if (reservations.isEmpty()) {
                            System.out.println("Aucune réservation trouvée.");
                        } else {
                            for (Reservation reservation : reservations) {
                                System.out.println(reservation);
                            }
                        }
                        break;

                    case 2:
                        // Ajouter une réservation
                        try {
                            System.out.print("Entrez l'ID de l'utilisateur : ");
                            int userId = scanner.nextInt();
                            System.out.print("Entrez l'ID du transport : ");
                            int transportId = scanner.nextInt();
                            System.out.print("Entrez l'ID du voyage : ");
                            int tripId = scanner.nextInt();
                            System.out.print("Entrez le numéro de siège : ");
                            int seatNumber = scanner.nextInt();
                            System.out.print("Entrez le type de siège : ");
                            String seatType = scanner.next();
                            System.out.print("Entrez le statut de la réservation (confirmé/annulé) : ");
                            String status = scanner.next();
                            System.out.print("Entrez le statut du paiement (payé/en attente) : ");
                            String paymentStatus = scanner.next();

                            // Convertir le timestamp actuel en java.sql.Timestamp
                            Timestamp reservationTime = new Timestamp(System.currentTimeMillis());

                            Reservation newReservation = new Reservation(userId, tripId, transportId, reservationTime, status, seatNumber, seatType, paymentStatus);
                            reservationService.add(newReservation);
                            System.out.println("Réservation ajoutée avec succès !");
                        } catch (Exception e) {
                            System.out.println("Erreur de saisie, veuillez entrer des valeurs valides.");
                            scanner.nextLine(); // Nettoyer le buffer
                        }
                        break;

                    case 3:
                        // Mise à jour d'une réservation
                        try {
                            System.out.print("Entrez l'ID de la réservation à modifier : ");
                            int resId = scanner.nextInt();

                            System.out.print("Entrez le nouveau statut de la réservation (confirmé/annulé) : ");
                            String newStatus = scanner.next();
                            System.out.print("Entrez le nouveau numéro de siège : ");
                            int newSeatNumber = scanner.nextInt();
                            System.out.print("Entrez le nouveau type de siège : ");
                            String newSeatType = scanner.next();
                            System.out.print("Entrez le nouveau statut du paiement (payé/en attente) : ");
                            String newPaymentStatus = scanner.next();

                            // Convertir le timestamp actuel en java.sql.Timestamp
                            Timestamp newReservationTime = new Timestamp(System.currentTimeMillis());

                            Reservation updatedReservation = new Reservation(resId, 0, 0, 0, newReservationTime, newStatus, newSeatNumber, newSeatType, newPaymentStatus);
                            reservationService.update(updatedReservation);
                            System.out.println("Réservation mise à jour avec succès !");
                        } catch (Exception e) {
                            System.out.println("Erreur de saisie, veuillez entrer des valeurs valides.");
                            scanner.nextLine(); // Nettoyer le buffer
                        }
                        break;

                    case 4:
                        // Suppression d'une réservation
                        try {
                            System.out.print("Entrez l'ID de la réservation à supprimer : ");
                            int resIdToDelete = scanner.nextInt();
                            reservationService.delete(resIdToDelete);
                            System.out.println("Réservation supprimée avec succès !");
                        } catch (Exception e) {
                            System.out.println("Erreur de saisie, veuillez entrer un ID valide.");
                            scanner.nextLine(); // Nettoyer le buffer
                        }
                        break;

                    case 5:
                        // Quitter le programme
                        System.out.println("Fermeture de l'application...");
                        return;

                    default:
                        System.out.println("Choix invalide, veuillez réessayer.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}