package tn.esprit.entities;

import java.util.Date;

public class Recu {
    private int id;
    private int userId; // ID de l'utilisateur
    private int factureId; // ID de la facture
    private Date datePaiement; // Date du paiement
    private float montant; // Montant payé

    // Constructeur
    public Recu(int id, int userId, int factureId, Date datePaiement, float montant) {
        this.id = id;
        this.userId = userId;
        this.factureId = factureId;
        this.datePaiement = datePaiement;
        this.montant = montant;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFactureId() {
        return factureId;
    }

    public void setFactureId(int factureId) {
        this.factureId = factureId;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "Recu{" +
                "id=" + id +
                ", userId=" + userId +
                ", factureId=" + factureId +
                ", datePaiement=" + datePaiement +
                ", montant=" + montant +
                '}';
    }
}
