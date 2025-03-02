package entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class RendezVous {
    private int idRendezVous;
    private LocalDate dateRendezVous;    // Stocke uniquement la date
    private LocalTime timeRendezVous;    // Stocke uniquement l'heure
    private String lieu;
    private String status;
    private int idMedecin;

    // Constructeur avec date et heure
    public RendezVous(LocalDate dateRendezVous, LocalTime timeRendezVous, String lieu, String status, int idMedecin) {
        this.dateRendezVous = dateRendezVous;
        this.timeRendezVous = timeRendezVous;
        this.lieu = lieu;
        this.status = status;
        this.idMedecin = idMedecin;
    }

    // Constructeur par défaut
    public RendezVous() {
    }

    // Constructeur avec id et date+heure séparées
    public RendezVous(int idRendezVous, LocalDate dateRendezVous, LocalTime timeRendezVous, String lieu, String status, int idMedecin) {
        this.idRendezVous = idRendezVous;
        this.dateRendezVous = dateRendezVous;
        this.timeRendezVous = timeRendezVous;
        this.lieu = lieu;
        this.status = status;
        this.idMedecin = idMedecin;
    }

    // Getter et Setter pour idRendezVous
    public int getIdRendezVous() {
        return idRendezVous;
    }

    public void setIdRendezVous(int idRendezVous) {
        this.idRendezVous = idRendezVous;
    }

    // Getter et Setter pour dateRendezVous
    public LocalDate getDateRendezVous() {
        return dateRendezVous;
    }

    public void setDateRendezVous(LocalDate dateRendezVous) {
        this.dateRendezVous = dateRendezVous;
    }

    // Getter et Setter pour timeRendezVous
    // Getter pour timeRendezVous sans paramètre
    public LocalTime getTimeRendezVous() {
        return timeRendezVous;
    }


    public void setTimeRendezVous(LocalTime timeRendezVous) {
        this.timeRendezVous = timeRendezVous;
    }

    // Getter et Setter pour lieu
    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    // Getter et Setter pour status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter et Setter pour idMedecin
    public int getIdMedecin() {
        return idMedecin;
    }

    public void setIdMedecin(int idMedecin) {
        this.idMedecin = idMedecin;
    }

    // Méthode toString pour afficher l'objet
    @Override
    public String toString() {
        return "RendezVous{" +
                "idRendezVous=" + idRendezVous +
                ", dateRendezVous=" + dateRendezVous +
                ", timeRendezVous=" + timeRendezVous +
                ", lieu='" + lieu + '\'' +
                ", status='" + status + '\'' +
                ", idMedecin=" + idMedecin +
                '}';
    }
}
