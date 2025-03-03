package tn.esprit.entities;

public class User {
    private String cin;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private String avatarUrl;

// Ajouter ce champ

    // Ajouter le getter/setter
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }// ADMIN/USER

    public User(String cin, String nom, String prenom, String email, String password, String role) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    public User() {}

    // Getters & Setters
    public String getCin() { return cin; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setCin(String cin) { this.cin = cin; }
    public void setNom(String nom) { this.nom = nom; }
}