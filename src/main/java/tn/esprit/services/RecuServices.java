package tn.esprit.services;

import tn.esprit.entities.Recu;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecuServices {

    Connection con;
    public RecuServices() {
        con = MyDatabase.getInstance().getCon();
    }

    // Lire la liste des reçus
    public List<Recu> readList() throws SQLException {
        String query = "SELECT * FROM `recu`";
        List<Recu> recuList = new ArrayList<>();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(query);
        while (rs.next()) {
            Recu r = new Recu(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("facture_id"),
                    rs.getDate("date_paiement"),
                    rs.getFloat("montant")
            );
            recuList.add(r);
        }
        return recuList;
    }

    // Ajouter un reçu
    public void addRecu(Recu recu) throws SQLException {
        String query = "INSERT INTO `recu`(`user_id`, `facture_id`, `date_paiement`, `montant`) VALUES (?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, recu.getUserId());
        ps.setInt(2, recu.getFactureId());
        ps.setDate(3, new java.sql.Date(recu.getDatePaiement().getTime()));
        ps.setFloat(4, recu.getMontant());
        ps.executeUpdate();
        System.out.println("Reçu added!");
    }

    // Mettre à jour un reçu
    public void update(Recu recu) throws SQLException {
        String query = "UPDATE `recu` SET `user_id` = ?, `facture_id` = ?, `date_paiement` = ?, `montant` = ? WHERE `id` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, recu.getUserId());
        ps.setInt(2, recu.getFactureId());
        ps.setDate(3, new java.sql.Date(recu.getDatePaiement().getTime()));
        ps.setFloat(4, recu.getMontant());
        ps.setInt(5, recu.getId());
        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Reçu updated successfully!");
        } else {
            System.out.println("No recu found with ID: " + recu.getId());
        }
    }

    // Supprimer un reçu
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM `recu` WHERE `id` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsDeleted = ps.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Reçu deleted successfully!");
        } else {
            System.out.println("No recu found with ID: " + id);
        }
    }
}
