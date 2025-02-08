package tn.esprit.services;

import tn.esprit.entities.Facture;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FactureServices implements IService<Facture> {

    Connection con;

    public FactureServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public List<Facture> readList() throws SQLException {
        String query = "SELECT * FROM `facture`";
        List<Facture> factures = new ArrayList<>();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(query);
        while (rs.next()) {
            Facture f = new Facture(
                    rs.getInt("id"),
                    rs.getDate("date_facture"),
                    rs.getFloat("prix_fact"), // Si la colonne prix_fact est de type FLOAT
                    rs.getString("type_facture"),
                    rs.getBoolean("state"),
                    rs.getInt("user_id")
            );

            factures.add(f);
        }
        return factures;
    }

    @Override
    public void add(Facture facture) throws SQLException {
        String query = "INSERT INTO `facture`(`date_facture`, `prix_fact`, `type_facture`, `state`, `user_id`) VALUES (?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setDate(1, new java.sql.Date(facture.getDate().getTime()));
        ps.setFloat(2, facture.getPrixFact());
        ps.setString(3, facture.getTypeFacture());
        ps.setBoolean(4, facture.isState());
        ps.setInt(5, facture.getUserId());

        // Exécution de l'insertion
        ps.executeUpdate();

        // Récupération de l'ID généré
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            facture.setId(generatedKeys.getInt(1));  // Récupération du premier ID généré
        } else {
            throw new SQLException("Failed to retrieve the generated ID for facture.");
        }
    }

    @Override
    public void addP(Facture facture) throws SQLException {
        add(facture); // Avoid duplicate code by calling the add method
    }

    @Override
    public void update(Facture facture) throws SQLException {
        String query = "UPDATE `facture` SET `date_facture` = ?, `type_facture` = ?, `state` = ?, `user_id` = ? WHERE `id` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setDate(1, new java.sql.Date(facture.getDate().getTime()));
        ps.setString(2, facture.getTypeFacture());
        ps.setBoolean(3, facture.isState());
        ps.setInt(4, facture.getUserId());  // L'ID de l'utilisateur
        ps.setInt(5, facture.getId());
        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Facture updated successfully!");
        } else {
            System.out.println("No facture found with ID: " + facture.getId());
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM `facture` WHERE `id` = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsDeleted = ps.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Facture deleted successfully!");
        } else {
            System.out.println("No facture found with ID: " + id);
        }
    }
}
