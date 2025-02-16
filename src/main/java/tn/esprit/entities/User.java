package tn.esprit.entities;

public class User {
    private String cin;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role; // ADMIN/USER

    public User(String cin, String nom, String prenom, String email, String password, String role) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public String getCin() { return cin; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
}