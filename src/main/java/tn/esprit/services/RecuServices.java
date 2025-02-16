package tn.esprit.services;

import tn.esprit.entities.Recu;
import tn.esprit.utils.MyDatabase;
import java.sql.*;

public class RecuServices {

    private final Connection con;

    public RecuServices() {
        con = MyDatabase.getInstance().getCon();
    }

    public void addRecu(Recu recu) throws SQLException {
        String query = "INSERT INTO recu (facture_id, date_paiement, montant) VALUES (?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, recu.getFactureId());
            ps.setDate(2, java.sql.Date.valueOf(recu.getDatePaiement()));
            ps.setDouble(3, recu.getMontant());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recu.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
}