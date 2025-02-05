package org.example.mdintech.service.ParkingModule;

import org.example.mdintech.Singleton.dbConnection;
import org.example.mdintech.entities.ParkingModule.ParkingTicket;
import org.example.mdintech.service.Iservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingTicketService implements Iservice<ParkingTicket> {

    private final Connection conn;

    public ParkingTicketService() {
        this.conn = dbConnection.getInstance().getConn();
    }

    @Override
    public void save(ParkingTicket obj) {
        String query = "INSERT INTO parking_tickets (userID, parkingID, issuingDate, expirationDate, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, obj.getUserID());
            stmt.setInt(2, obj.getParkingID());
            stmt.setTimestamp(3, new Timestamp(obj.getIssuingDate().getTime()));
            stmt.setTimestamp(4, new Timestamp(obj.getExpirationDate().getTime()));
            stmt.setBoolean(5, obj.isStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        obj.setId(generatedKeys.getInt(1));
                        System.out.println("Parking ticket saved with ID: " + obj.getId());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ParkingTicket obj) {
        String query = "UPDATE parking_tickets SET userID=?, parkingID=?, issuingDate=?, expirationDate=?, status=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, obj.getUserID());
            stmt.setInt(2, obj.getParkingID());
            stmt.setTimestamp(3, new Timestamp(obj.getIssuingDate().getTime()));
            stmt.setTimestamp(4, new Timestamp(obj.getExpirationDate().getTime()));
            stmt.setBoolean(5, obj.isStatus());
            stmt.setInt(6, obj.getId());

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Parking ticket updated successfully." : "No ticket found to update.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(ParkingTicket obj) {
        String query = "DELETE FROM parking_tickets WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, obj.getId());
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Parking ticket deleted successfully." : "No ticket found to delete.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ParkingTicket findById(int id) {
        String query = "SELECT * FROM parking_tickets WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ParkingTicket(
                        rs.getInt("userID"),
                        rs.getInt("parkingID"),
                        rs.getTimestamp("issuingDate"),
                        rs.getTimestamp("expirationDate"),
                        rs.getBoolean("status") ? "Active" : "Inactive"
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ParkingTicket> findAll() {
        List<ParkingTicket> tickets = new ArrayList<>();
        String query = "SELECT * FROM parking_tickets";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ParkingTicket ticket = new ParkingTicket(
                        rs.getInt("userID"),
                        rs.getInt("parkingID"),
                        rs.getTimestamp("issuingDate"),
                        rs.getTimestamp("expirationDate"),
                        rs.getBoolean("status") ? "Active" : "Inactive"
                );
                ticket.setId(rs.getInt("id"));
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }
}
