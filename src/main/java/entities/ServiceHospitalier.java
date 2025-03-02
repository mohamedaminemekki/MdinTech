package entities;

public class ServiceHospitalier {
    private int idService;
    private String nomService;
    private String description;

    // Constructeurs
    public ServiceHospitalier() {
    }

    public ServiceHospitalier(String nomService, String description) {
        this.nomService = nomService;
        this.description = description;
    }

    public ServiceHospitalier(int idService, String nomService, String description) {
        this.idService = idService;
        this.nomService = nomService;
        this.description = description;
    }

    // Getters et Setters
    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getNomService() {
        return nomService;
    }

    public void setNomService(String nomService) {
        this.nomService = nomService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "ServiceHospitalier{" +
                "idService=" + idService +
                ", nomService='" + nomService + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
