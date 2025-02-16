package tn.esprit.entities;

import java.time.LocalDate;

public class Recu {
    private int id;
    private int factureId;
    private LocalDate datePaiement;
    private double montant;

    // Constructeur simplifié
    public Recu(int id, int factureId, LocalDate datePaiement, double montant) {
        this.id = id;
        this.factureId = factureId;
        this.datePaiement = datePaiement;
        this.montant = montant;
    }

    // Getters/Setters
    public int getId() { return id; }
    public int getFactureId() { return factureId; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public double getMontant() { return montant; }

    public void setId(int id) { this.id = id; }
    public void setFactureId(int factureId) { this.factureId = factureId; }


    // Setters si nécessaire...
}