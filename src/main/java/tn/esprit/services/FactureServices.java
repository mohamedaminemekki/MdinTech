package tn.esprit.services;

import tn.esprit.entities.Facture;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FactureServices implements IService<Facture> {

    private final Connection con;

    public FactureServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public List<Facture> readList() throws SQLException {
        String query = "SELECT * FROM facture";
        List<Facture> factures = new ArrayList<>();

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Facture f = new Facture(
                        rs.getInt("id"),
                        rs.getDate("date_facture").toLocalDate(),
                        rs.getDate("date_limite_paiement").toLocalDate(),
                        rs.getFloat("prix_fact"),
                        rs.getString("type_facture"),
                        rs.getBoolean("state"),
                        rs.getString("user_cin")
                );
                factures.add(f);
            }
        }
        return factures;
    }

    public List<Facture> getFacturesByUser(String cin) throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM facture WHERE user_cin = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, cin);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Facture facture = new Facture(
                        rs.getInt("id"),
                        rs.getDate("date_facture").toLocalDate(),
                        rs.getDate("date_limite_paiement").toLocalDate(),
                        rs.getFloat("prix_fact"),
                        rs.getString("type_facture"),
                        rs.getBoolean("state"),
                        rs.getString("user_cin")
                );

                // Ajouter cette ligne pour récupérer la date de paiement
                Date datePaiement = rs.getDate("date_paiement");
                if (datePaiement != null) {
                    facture.setDatePaiement(datePaiement.toLocalDate());
                }

                factures.add(facture);
            }
        }
        return factures;
    }


    @Override
    public void add(Facture facture) throws SQLException {
        String query = "INSERT INTO facture (date_facture, date_limite_paiement, prix_fact, type_facture, state, user_cin) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, java.sql.Date.valueOf(facture.getDateFacture()));
            ps.setDate(2, java.sql.Date.valueOf(facture.getDateLimitePaiement()));
            ps.setFloat(3, facture.getPrixFact());
            ps.setString(4, facture.getTypeFacture());
            ps.setBoolean(5, facture.isState());
            ps.setString(6, facture.getUserCIN());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    facture.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    public void updatePaymentStatus(Facture facture) throws SQLException {
        String query = "UPDATE facture SET state = ?, date_paiement = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setBoolean(1, facture.isState());
            ps.setDate(2, Date.valueOf(facture.getDatePaiement()));
            ps.setInt(3, facture.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Facture facture) throws SQLException {
        String query = "UPDATE facture SET "
                + "date_facture = ?, "
                + "date_limite_paiement = ?, "
                + "prix_fact = ?, "
                + "type_facture = ?, "
                + "state = ?, "
                + "date_paiement = ?, " // Ajouté
                + "user_cin = ? "
                + "WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(facture.getDateFacture()));
            ps.setDate(2, Date.valueOf(facture.getDateLimitePaiement()));
            ps.setFloat(3, facture.getPrixFact());
            ps.setString(4, facture.getTypeFacture());
            ps.setBoolean(5, facture.isState());
            ps.setDate(6, facture.getDatePaiement() != null ?
                    Date.valueOf(facture.getDatePaiement()) : null);
            ps.setString(7, facture.getUserCIN());
            ps.setInt(8, facture.getId());

            ps.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException {
        String query = "DELETE FROM facture WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Méthode optionnelle si nécessaire
    @Override
    public void addP(Facture facture) throws SQLException {
        add(facture); // Ou implémentation spécifique
    }
    public List<Facture> getUnpaidFacturesByUser(String cin) throws SQLException {
        String query = "SELECT * FROM facture WHERE user_cin = ? AND state = false";
        return executeFactureQuery(query, cin);
    }

    public List<Facture> getPaidFacturesByUser(String cin) throws SQLException {
        String query = "SELECT * FROM facture WHERE user_cin = ? AND state = true";
        return executeFactureQuery(query, cin);
    }

    private List<Facture> executeFactureQuery(String query, String cin) throws SQLException {
        List<Facture> factures = new ArrayList<>();
        try (
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, cin);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date datePaiement = rs.getDate("date_paiement");
                    LocalDate localDatePaiement = (datePaiement != null) ? datePaiement.toLocalDate() : null;
                    Facture facture = new Facture(
                            rs.getInt("id"),
                            rs.getDate("date_facture").toLocalDate(),
                            rs.getDate("date_limite_paiement").toLocalDate(),
                            rs.getFloat("prix_fact"),
                            rs.getString("type_facture"),
                            rs.getBoolean("state"),
                            rs.getString("user_cin")
                    );
                    if (facture.isState()) {
                        facture.setDatePaiement(rs.getDate("date_paiement").toLocalDate());
                    }
                    factures.add(facture);
                }
            }
        }
        return factures;
    }

}