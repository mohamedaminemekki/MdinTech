package services;

import entities.RendezVous;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RendezVousServices implements IService<RendezVous> {

    Connection con;

    public RendezVousServices() {
        con = MyDataBase.getInstance().getCon();
    }

    // Fonction readList pour récupérer tous les rendez-vous
    public List<RendezVous> readList() throws SQLException {
        String query = "SELECT * FROM `rendezvous`";
        List<RendezVous> rendezVousList = new ArrayList<>();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(query);
        while (rs.next()) {
            // Récupérer la date et l'heure séparées
            LocalDate date = rs.getDate("dateRendezVous").toLocalDate();
            LocalTime time = rs.getTime("timeRendezVous").toLocalTime();

            // Créer un objet RendezVous avec la date et l'heure séparées
            RendezVous r = new RendezVous(
                    rs.getInt("idRendezVous"),
                    date,
                    time,
                    rs.getString("lieu"),
                    rs.getString("status"),
                    rs.getInt("idMedecin")
            );
            rendezVousList.add(r);
        }
        return rendezVousList;
    }

    // Fonction pour ajouter un rendez-vous
    public void add(RendezVous rendezVous) throws SQLException {
        String query = "INSERT INTO `rendezvous` (`dateRendezVous`, `timeRendezVous`, `lieu`, `status`, `idMedecin`) VALUES (?, ?, ?, ?, ?)";

        // Préparer la requête avec des paramètres
        PreparedStatement ps = con.prepareStatement(query);
        ps.setDate(1, Date.valueOf(rendezVous.getDateRendezVous())); // Conversion LocalDate en Date
        ps.setTime(2, Time.valueOf(rendezVous.getTimeRendezVous())); // Conversion LocalTime en Time
        ps.setString(3, rendezVous.getLieu());
        ps.setString(4, rendezVous.getStatus());
        ps.setInt(5, rendezVous.getIdMedecin());

        // Exécuter la requête pour insérer le rendez-vous
        ps.executeUpdate();
        System.out.println("Rendez-vous ajouté !");
    }


    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM `rendezvous` WHERE `idRendezVous` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);  // Utilisation de l'ID passé en paramètre
        ps.executeUpdate();
    }

    // Fonction pour mettre à jour un rendez-vous
    public void update(RendezVous rendezVous) throws SQLException {
        String query = "UPDATE rendezvous SET dateRendezVous = ?, timeRendezVous = ?, lieu = ?, status = ?, idMedecin = ? WHERE idRendezVous = ?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setDate(1, Date.valueOf(rendezVous.getDateRendezVous()));  // Conversion LocalDate en Date
        ps.setTime(2, Time.valueOf(rendezVous.getTimeRendezVous()));  // Conversion LocalTime en Time
        ps.setString(3, rendezVous.getLieu());
        ps.setString(4, rendezVous.getStatus());
        ps.setInt(5, rendezVous.getIdMedecin());
        ps.setInt(6, rendezVous.getIdRendezVous());  // ID du rendez-vous à modifier

        int rowsAffected = ps.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Rendez-vous modifié avec succès.");
        } else {
            System.out.println("Aucun rendez-vous trouvé avec l'ID : " + rendezVous.getIdRendezVous());
        }
    }
}
