package services;

import entities.RendezVous;
import entities.ServiceHospitalier;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceHospitalierServices implements IService<ServiceHospitalier> {

    Connection con;

    public ServiceHospitalierServices() {
        con = MyDataBase.getInstance().getCon();
    }

    // Fonction readList pour récupérer tous les services hospitaliers
    public List<ServiceHospitalier> readList() throws SQLException {
        String query = "SELECT * FROM `servicehospitalier`";
        List<ServiceHospitalier> serviceList = new ArrayList<>();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(query);
        while (rs.next()) {
            ServiceHospitalier s = new ServiceHospitalier(
                    rs.getInt("idService"),
                    rs.getString("nomService"),
                    rs.getString("description")
            );
            serviceList.add(s);
        }
        return serviceList;
    }


    public void add(ServiceHospitalier service) throws SQLException {
        String query = "INSERT INTO `servicehospitalier` (`nomService`, `description`) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, service.getNomService());
        ps.setString(2, service.getDescription());

        ps.executeUpdate();
        System.out.println("Service hospitalier ajouté !");
    }
    public void delete(int id) throws SQLException{
        String query = "DELETE FROM `servicehospitalier` WHERE `idService` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Service hospitalier supprimé !");
    }
    public void update(ServiceHospitalier serviceHospitalier) throws SQLException{
        String query = "UPDATE `servicehospitalier` SET `nomService` = ?, `description` = ? WHERE `idService` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, serviceHospitalier.getNomService());
        ps.setString(2, serviceHospitalier.getDescription());
        ps.setInt(3, serviceHospitalier.getIdService());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Service hospitalier modifié avec succès.");
        } else {
            System.out.println("Aucun service hospitalier trouvé avec l'ID : " + serviceHospitalier.getIdService());
        }
    }


    public int getServiceIdFromName(String serviceName) throws SQLException {
        String query = "SELECT idService FROM servicehospitalier WHERE nomService = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, serviceName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idService");
                }
            }
        }
        return -1; // Retourne -1 si aucun service trouvé
    }


}






