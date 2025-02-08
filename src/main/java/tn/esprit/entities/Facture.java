package tn.esprit.entities;

import java.util.Date;

public class Facture {
    private int id;
    private Date date;
    private float prixFact;
    private String typeFacture;
    private boolean state;
    private int userId;  // L'ID de l'utilisateur associé à la facture

    // Constructeur avec les 6 paramètres
    public Facture(int id, Date date, float prixFact, String typeFacture, boolean state, int userId) {
        this.id = id;
        this.date = date;
        this.prixFact = prixFact;
        this.typeFacture = typeFacture;
        this.state = state;
        this.userId = userId;  // Initialisation de l'ID de l'utilisateur
    }

    // Constructeur sans userId pour les cas où il n'est pas fourni (utile pour la création de Facture sans ID utilisateur)
    public Facture(int id, Date date, float prixFact, String typeFacture, boolean state) {
        this.id = id;
        this.date = date;
        this.prixFact = prixFact;
        this.typeFacture = typeFacture;
        this.state = state;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getPrixFact() {
        return prixFact;
    }

    public void setPrixFact(float prixFact) {
        this.prixFact = prixFact;
    }

    public String getTypeFacture() {
        return typeFacture;
    }

    public void setTypeFacture(String typeFacture) {
        this.typeFacture = typeFacture;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", date=" + date +
                ", prixFact=" + prixFact +
                ", typeFacture='" + typeFacture + '\'' +
                ", state=" + state +
                ", userId=" + userId +
                '}';
    }
}
