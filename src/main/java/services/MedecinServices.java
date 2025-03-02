package services;

import entities.Medecin;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinServices implements IService<Medecin> {

    private Connection con;

    public MedecinServices() {
        con = MyDataBase.getInstance().getCon();
    }

    // Ajouter un médecin
    public void add(Medecin medecin) throws SQLException {
        String query = "INSERT INTO medecin (nomM, prenomM, specialite, contact, idService) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, medecin.getNomM());
        ps.setString(2, medecin.getPrenomM());
        ps.setString(3, medecin.getSpecialite());
        ps.setInt(4, medecin.getContact());
        ps.setInt(5, medecin.getIdService());
        ps.executeUpdate();
        System.out.println("Médecin ajouté avec succès !");
    }

    // Lire tous les médecins
    public List<Medecin> readList() throws SQLException {
        String query = "SELECT * FROM medecin";
        List<Medecin> medecinsList = new ArrayList<>();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(query);
        while (rs.next()) {
            Medecin medecin = new Medecin(
                    rs.getInt("idMedecin"),
                    rs.getString("nomM"),
                    rs.getString("prenomM"),
                    rs.getString("specialite"),
                    rs.getInt("contact"),
                    rs.getInt("idService")
            );
            medecinsList.add(medecin);
        }
        return medecinsList;
    }

    // Mettre à jour un médecin
    public void update(Medecin medecin) throws SQLException {
        String query = "UPDATE medecin SET nomM = ?, prenomM = ?, specialite = ?, contact = ?, idService = ? WHERE idMedecin = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, medecin.getNomM());
        ps.setString(2, medecin.getPrenomM());
        ps.setString(3, medecin.getSpecialite());
        ps.setInt(4, medecin.getContact());
        ps.setInt(5, medecin.getIdService());
        ps.setInt(6, medecin.getIdMedecin());
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Médecin mis à jour avec succès.");
        } else {
            System.out.println("Aucun médecin trouvé avec l'ID : " + medecin.getIdMedecin());
        }
    }

    // Supprimer un médecin
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM medecin WHERE idMedecin = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Médecin supprimé avec succès.");
    }


    public List<Medecin> getMedecinsByService(int idService) throws SQLException {
        String query = "SELECT * FROM medecin WHERE idService = ?";
        List<Medecin> medecinsList = new ArrayList<>();
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, idService);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Medecin medecin = new Medecin(
                    rs.getInt("idMedecin"),
                    rs.getString("nomM"),
                    rs.getString("prenomM"),
                    rs.getString("specialite"),
                    rs.getInt("contact"),
                    rs.getInt("idService")
            );
            medecinsList.add(medecin);
        }
        return medecinsList;
    }



    public int getMedecinIdByName(String nomMedecin) throws SQLException {
        // Séparer le nom et le prénom du médecin
        String[] parts = nomMedecin.split(" ");
        String nom = parts[0]; // Nom du médecin
        String prenom = parts[1]; // Prénom du médecin

        // Requête SQL pour récupérer l'ID du médecin
        String query = "SELECT idMedecin FROM medecin WHERE nomM = ? AND prenomM = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            // Définir les paramètres de la requête
            ps.setString(1, nom);
            ps.setString(2, prenom);

            // Exécuter la requête
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Retourner l'ID du médecin
                    return rs.getInt("idMedecin");
                } else {
                    throw new SQLException("Médecin non trouvé : " + nomMedecin);
                }
            }
        }
    }

}
