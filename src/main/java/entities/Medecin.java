package entities;

public class Medecin {
    private int idMedecin;
    private String nomM;
    private String prenomM;
    private String specialite;
    private int contact;
    private int idService;  // Clé étrangère vers la table servicehopitalier

    // Constructeurs
    public Medecin(int idMedecin, String nomM, String prenomM, String specialite, int contact, int idService) {
        this.idMedecin = idMedecin;
        this.nomM = nomM;
        this.prenomM = prenomM;
        this.specialite = specialite;
        this.contact = contact;
        this.idService = idService;
    }

    public Medecin(String nomM, String prenomM, String specialite, int contact, int idService) {
        this.nomM = nomM;
        this.prenomM = prenomM;
        this.specialite = specialite;
        this.contact = contact;
        this.idService = idService;
    }

    public Medecin() {}

    // Getters et Setters
    public int getIdMedecin() {
        return idMedecin;
    }

    public void setIdMedecin(int idMedecin) {
        this.idMedecin = idMedecin;
    }

    public String getNomM() {
        return nomM;
    }

    public void setNomM(String nomM) {
        this.nomM = nomM;
    }

    public String getPrenomM() {
        return prenomM;
    }

    public void setPrenomM(String prenomM) {
        this.prenomM = prenomM;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public int getContact() {
        return contact;
    }

    public void setContact(int contact) {
        this.contact = contact;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    @Override
    public String toString() {
        return "Medecin{" +
                "idMedecin=" + idMedecin +
                ", nomM='" + nomM + '\'' +
                ", prenomM='" + prenomM + '\'' +
                ", specialite='" + specialite + '\'' +
                ", contact='" + contact + '\'' +
                ", idService=" + idService +
                '}';
    }
}
