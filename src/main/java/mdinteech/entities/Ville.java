package mdinteech.entities;

public class Ville {
    private String nom;
    private String histoire;
    private String anecdotes;
    private String activites;
    private String gastronomie;
    private String nature;
    private String histoireInteractive;

    public Ville(String nom, String histoire, String anecdotes, String activites,
                 String gastronomie, String nature, String histoireInteractive) {
        this.nom = nom;
        this.histoire = histoire;
        this.anecdotes = anecdotes;
        this.activites = activites;
        this.gastronomie = gastronomie;
        this.nature = nature;
        this.histoireInteractive = histoireInteractive;
    }

    // Getters et Setters
    public String getNom() { return nom; }
    public String getHistoire() { return histoire; }
    public String getAnecdotes() { return anecdotes; }
    public String getActivites() { return activites; }
    public String getGastronomie() { return gastronomie; }
    public String getNature() { return nature; }
    public String getHistoireInteractive() { return histoireInteractive; }

    public void setNom(String nom) { this.nom = nom; }
    public void setHistoire(String histoire) { this.histoire = histoire; }
    public void setAnecdotes(String anecdotes) { this.anecdotes = anecdotes; }
    public void setActivites(String activites) { this.activites = activites; }
    public void setGastronomie(String gastronomie) { this.gastronomie = gastronomie; }
    public void setNature(String nature) { this.nature = nature; }
    public void setHistoireInteractive(String histoireInteractive) { this.histoireInteractive = histoireInteractive; }
}