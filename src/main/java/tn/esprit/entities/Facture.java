package tn.esprit.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Facture {
    private int id;
    private LocalDate dateFacture;
    private LocalDate dateLimitePaiement;
    private float prixFact;
    private String typeFacture;
    private boolean state;
    private String userCIN;
    private List<Recu> paiements = new ArrayList<>();
    private LocalDate datePaiement;

    public Facture(int id, LocalDate dateFacture, LocalDate dateLimitePaiement, float prixFact,
                   String typeFacture, boolean state, String userCIN) {
        this.id = id;
        this.dateFacture = dateFacture;
        this.dateLimitePaiement = dateLimitePaiement;
        this.prixFact = prixFact;
        this.typeFacture = typeFacture;
        this.state = state;
        this.userCIN = userCIN;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }

    public LocalDate getDateLimitePaiement() { return dateLimitePaiement; }
    public void setDateLimitePaiement(LocalDate dateLimitePaiement) { this.dateLimitePaiement = dateLimitePaiement; }

    public float getPrixFact() { return prixFact; }
    public void setPrixFact(float prixFact) { this.prixFact = prixFact; }

    public String getTypeFacture() { return typeFacture; }
    public void setTypeFacture(String typeFacture) { this.typeFacture = typeFacture; }

    public boolean isState() { return state; }
    public void setState(boolean state) { this.state = state; }

    public String getUserCIN() { return userCIN; }
    public void setUserCIN(String userCIN) { this.userCIN = userCIN; }
    public List<Recu> getPaiements() { return paiements; }
    public void setPaiements(List<Recu> paiements) { this.paiements = paiements; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", dateFacture=" + dateFacture +
                ", dateLimitePaiement=" + dateLimitePaiement +
                ", prixFact=" + prixFact +
                ", typeFacture='" + typeFacture + '\'' +
                ", state=" + state +
                ", userCIN='" + userCIN + '\'' +
                '}';
    }
}