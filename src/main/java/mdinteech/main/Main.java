package mdinteech.main;

import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static TripService tripService;
    private static ReservationService reservationService;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Établir la connexion à la base de données
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/city_transport", "root", "");

            // Initialiser les services
            tripService = new TripService(conn);
            reservationService = new ReservationService(conn);

            // Afficher le menu principal
            boolean running = true;
            while (running) {
                System.out.println("\n=== Menu Principal ===");
                System.out.println("1. Gestion des voyages");
                System.out.println("2. Gestion des réservations");
                System.out.println("3. Quitter");
                System.out.print("Choisissez une option : ");

                int mainChoice = scanner.nextInt();
                scanner.nextLine(); // Pour consommer la nouvelle ligne

                switch (mainChoice) {
                    case 1:
                        manageTrips();
                        break;
                    case 2:
                        manageReservations();
                        break;
                    case 3:
                        running = false;
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Option invalide. Veuillez réessayer.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Gestion des voyages
    private static void manageTrips() {
        boolean backToMain = false;
        while (!backToMain) {
            System.out.println("\n=== Gestion des Voyages ===");
            System.out.println("1. Afficher tous les voyages");
            System.out.println("2. Ajouter un voyage");
            System.out.println("3. Mettre à jour un voyage");
            System.out.println("4. Supprimer un voyage");
            System.out.println("5. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            switch (choice) {
                case 1:
                    displayAllTrips();
                    break;
                case 2:
                    addTrip();
                    break;
                case 3:
                    updateTrip();
                    break;
                case 4:
                    deleteTrip();
                    break;
                case 5:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    // Gestion des réservations
    private static void manageReservations() {
        boolean backToMain = false;
        while (!backToMain) {
            System.out.println("\n=== Gestion des Réservations ===");
            System.out.println("1. Afficher toutes les réservations");
            System.out.println("2. Ajouter une réservation");
            System.out.println("3. Mettre à jour une réservation");
            System.out.println("4. Supprimer une réservation");
            System.out.println("5. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            switch (choice) {
                case 1:
                    displayAllReservations();
                    break;
                case 2:
                    addReservation();
                    break;
                case 3:
                    updateReservation();
                    break;
                case 4:
                    deleteReservation();
                    break;
                case 5:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    // Méthodes pour la gestion des voyages
    private static void displayAllTrips() {
        try {
            List<Trip> trips = tripService.readList();
            if (trips.isEmpty()) {
                System.out.println("Aucun voyage trouvé.");
            } else {
                System.out.println("\n=== Liste des voyages ===");
                for (Trip trip : trips) {
                    System.out.println(trip);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des voyages : " + e.getMessage());
        }
    }

    private static void addTrip() {
        try {
            System.out.println("\n=== Ajouter un voyage ===");
            System.out.print("Départ : ");
            String departure = scanner.nextLine();

            System.out.print("Destination : ");
            String destination = scanner.nextLine();

            System.out.print("Heure de départ (AAAA-MM-JJ HH:MM:SS) : ");
            String departureTimeStr = scanner.nextLine();

            System.out.print("Heure d'arrivée (AAAA-MM-JJ HH:MM:SS) : ");
            String arrivalTimeStr = scanner.nextLine();

            System.out.print("ID du transport : ");
            int transportId = scanner.nextInt();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            System.out.print("Nom du transport : ");
            String transportName = scanner.nextLine();

            System.out.print("Prix : ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            System.out.print("Date du voyage (AAAA-MM-JJ) : ");
            String dateStr = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            // Créer un nouvel objet Trip
            Trip newTrip = new Trip(0, transportId,
                    java.sql.Timestamp.valueOf(departureTimeStr),
                    java.sql.Timestamp.valueOf(arrivalTimeStr),
                    price, departure, destination, transportName);

            // Ajouter le voyage
            tripService.add(newTrip);
            System.out.println("Voyage ajouté avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du voyage : " + e.getMessage());
        }
    }

    private static void updateTrip() {
        try {
            System.out.println("\n=== Mettre à jour un voyage ===");
            System.out.print("ID du voyage à mettre à jour : ");
            int tripId = scanner.nextInt();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            // Récupérer le voyage existant
            Trip trip = tripService.getById(tripId);
            if (trip == null) {
                System.out.println("Aucun voyage trouvé avec cet ID.");
                return;
            }

            System.out.println("Voyage actuel : " + trip);

            // Demander les nouvelles valeurs
            System.out.print("Nouveau départ : ");
            String departure = scanner.nextLine();

            System.out.print("Nouvelle destination : ");
            String destination = scanner.nextLine();

            System.out.print("Nouvelle heure de départ (AAAA-MM-JJ HH:MM:SS) : ");
            String departureTimeStr = scanner.nextLine();

            System.out.print("Nouvelle heure d'arrivée (AAAA-MM-JJ HH:MM:SS) : ");
            String arrivalTimeStr = scanner.nextLine();

            System.out.print("Nouvel ID du transport : ");
            int transportId = scanner.nextInt();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            System.out.print("Nouveau nom du transport : ");
            String transportName = scanner.nextLine();

            System.out.print("Nouveau prix : ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            System.out.print("Nouvelle date du voyage (AAAA-MM-JJ) : ");
            String dateStr = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            // Mettre à jour l'objet Trip
            trip.setDeparture(departure);
            trip.setDestination(destination);
            trip.setDepartureTime(java.sql.Timestamp.valueOf(departureTimeStr));
            trip.setArrivalTime(java.sql.Timestamp.valueOf(arrivalTimeStr));
            trip.setTransportId(transportId);
            trip.setTransportName(transportName);
            trip.setPrice(price);


            // Mettre à jour le voyage dans la base de données
            tripService.update(trip);
            System.out.println("Voyage mis à jour avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du voyage : " + e.getMessage());
        }
    }

    private static void deleteTrip() {
        try {
            System.out.println("\n=== Supprimer un voyage ===");
            System.out.print("ID du voyage à supprimer : ");
            int tripId = scanner.nextInt();
            scanner.nextLine(); // Pour consommer la nouvelle ligne

            // Supprimer le voyage
            tripService.delete(tripId);
            System.out.println("Voyage supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du voyage : " + e.getMessage());
        }
    }

    // Méthodes pour la gestion des réservations
    private static void displayAllReservations() {
        try {
            List<Reservation> reservations = reservationService.readList();
            if (reservations.isEmpty()) {
                System.out.println("Aucune réservation trouvée.");
            } else {
                System.out.println("\n=== Liste des réservations ===");
                for (Reservation reservation : reservations) {
                    System.out.println(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations : " + e.getMessage());
        }
    }

    private static void addReservation() {
        try {
            System.out.println("\n=== Ajouter une réservation ===");
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
    }

    private static void updateReservation() {
        try {
            System.out.println("\n=== Mettre à jour une réservation ===");
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
    }

    private static void deleteReservation() {
        try {
            System.out.println("\n=== Supprimer une réservation ===");
            System.out.print("Entrez l'ID de la réservation à supprimer : ");
            int resIdToDelete = scanner.nextInt();
            reservationService.delete(resIdToDelete);
            System.out.println("Réservation supprimée avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur de saisie, veuillez entrer un ID valide.");
            scanner.nextLine(); // Nettoyer le buffer
        }
    }
}