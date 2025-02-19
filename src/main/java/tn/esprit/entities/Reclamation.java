package tn.esprit.entities;

public class Reclamation {
    private int id;
    private int client_id;
    private String datee;
    private String description;
    private Boolean state;
    private String type;
    private String photo;

    public Reclamation(int id, int client_id, String datee, String description, Boolean state, String type, String photo) {
        this.id = id;
        this.client_id = client_id;
        this.datee = datee;
        this.description = description;
        this.state = state;
        this.type = type;
        this.photo = photo;
    }

    public Reclamation(int client_id, String datee, String description, Boolean state, String type, String photo) {
        this.client_id = client_id;
        this.datee = datee;
        this.description = description;
        this.state = state;
        this.type = type;
        this.photo = photo;
    }

    public Reclamation() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClient_id() { return client_id; }
    public void setClient_id(int client_id) { this.client_id = client_id; }

    public String getDatee() { return datee; }
    public void setDatee(String datee) { this.datee = datee; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getState() { return state; }
    public void setState(Boolean state) { this.state = state; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", client_id=" + client_id +
                ", datee='" + datee + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", type='" + type + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
