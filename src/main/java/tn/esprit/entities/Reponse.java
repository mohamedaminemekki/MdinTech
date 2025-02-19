package tn.esprit.entities;

public class Reponse {
    private int id;
    private int reclamationId; // Foreign key referencing Reclamation
    private String message;
    private String datee;

    // Constructors
    public Reponse() {}

    public Reponse(int id, int reclamationId, String message, String datee) {
        this.id = id;
        this.reclamationId = reclamationId;
        this.message = message;
        this.datee = datee;
    }

    public Reponse(int reclamationId, String message, String datee) {
        this.reclamationId = reclamationId;
        this.message = message;
        this.datee = datee;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatee() {
        return datee;
    }

    public void setDatee(String datee) {
        this.datee = datee;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", reclamationId=" + reclamationId +
                ", message='" + message + '\'' +
                ", datee='" + datee + '\'' +
                '}';
    }
}
