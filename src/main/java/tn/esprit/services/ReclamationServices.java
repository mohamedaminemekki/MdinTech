package tn.esprit.services;

import tn.esprit.entities.Reclamation;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationServices implements IService<Reclamation> {
    private Connection con;

    public ReclamationServices() {
        con = MyDataBase.getInstance().getConn();
    }

    @Override
    public List<Reclamation> readList() throws SQLException {
        String query = "SELECT * FROM reclamation";
        List<Reclamation> reclamations = new ArrayList<>();

        try (Statement stm = con.createStatement(); ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("datee"),
                        rs.getString("description"),
                        rs.getInt("state") == 1,
                        rs.getString("type"),
                        rs.getString("photo")
                );
                reclamations.add(r);
            }
        }

        return reclamations;
    }

    @Override
    public void add(Reclamation reclamation) throws SQLException {
        String query = "INSERT INTO reclamation (client_id, datee, description, state, type, photo) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, reclamation.getClient_id());
            pstmt.setString(2, reclamation.getDatee());
            pstmt.setString(3, reclamation.getDescription());
            pstmt.setBoolean(4, reclamation.getState());
            pstmt.setString(5, reclamation.getType());
            pstmt.setString(6, reclamation.getPhoto());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reclamation.setId(generatedKeys.getInt(1));
                }
                System.out.println("Réclamation ajoutée avec succès !");
            }
        }
    }

    @Override
    public void update(Reclamation reclamation) throws SQLException {
        String query = "UPDATE reclamation SET client_id = ?, description = ?, state = ?, datee = ?, type = ?, photo = ? WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, reclamation.getClient_id());
            pstmt.setString(2, reclamation.getDescription());
            pstmt.setInt(3, reclamation.getState() ? 1 : 0);
            pstmt.setString(4, reclamation.getDatee());
            pstmt.setString(5, reclamation.getType());
            pstmt.setString(6, reclamation.getPhoto());
            pstmt.setInt(7, reclamation.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Reclamation updated successfully!");
            } else {
                System.out.println("No reclamation found with ID: " + reclamation.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM reclamation WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Reclamation deleted successfully!");
            } else {
                System.out.println("No reclamation found with ID: " + id);
            }
        }
    }

    @Override
    public void addP(Reclamation reclamation) throws SQLException {
        add(reclamation); // Calls the add() method, since they have the same functionality
    }

    public void updateReclamationState(Reclamation rec) throws SQLException {
        String query = "UPDATE reclamation SET state = ? WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setBoolean(1, rec.getState()); // Update the state (True for treated, False for non-treated)
            pstmt.setInt(2, rec.getId()); // Use the ID of the reclamation to identify it
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Reclamation state updated successfully!");
            } else {
                System.out.println("No reclamation found with ID: " + rec.getId());
            }
        }
    }

}
