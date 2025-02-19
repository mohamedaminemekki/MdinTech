package org.example.mdintech.service.ParkingModule;

import org.example.mdintech.Singleton.dbConnection;
import org.example.mdintech.entities.ParkingModule.ParkingTicket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingTicketService {
    private final Connection conn;
    private ParkingSlotService parkingSlotService;

    public ParkingTicketService() {
        this.conn = dbConnection.getInstance().getConn();
        this.parkingSlotService = new ParkingSlotService();
    }

    // Save a new parking ticket
    public void save(ParkingTicket ticket) {
        String query = "INSERT INTO parking_ticket (user_id, parking_id, parking_slot_id, issuing_date, expiration_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ticket.getUserID());
            stmt.setInt(2, ticket.getParkingID());
            stmt.setInt(3, ticket.getParkingSlotID());
            stmt.setTimestamp(4, new Timestamp(ticket.getIssuingDate().getTime()));
            stmt.setTimestamp(5, new Timestamp(ticket.getExpirationDate().getTime()));
            stmt.setBoolean(6, ticket.isStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ticket.setId(generatedKeys.getInt(1));
                        System.out.println("Parking ticket saved with ID: " + ticket.getId());
                    }
                }

                this.parkingSlotService.updateSlotStatus(ticket.getParkingSlotID(), false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update an existing parking ticket
    public void update(ParkingTicket ticket) {
        String query = "UPDATE parking_ticket SET user_id=?, parking_id=?, parking_slot_id=?, issuing_date=?, expiration_date=?, status=? WHERE TicketID=?";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ticket.getUserID());
            stmt.setInt(2, ticket.getParkingID());
            stmt.setInt(3, ticket.getParkingSlotID()); // New addition
            stmt.setTimestamp(4, new Timestamp(ticket.getIssuingDate().getTime()));
            stmt.setTimestamp(5, new Timestamp(ticket.getExpirationDate().getTime()));
            stmt.setBoolean(6, ticket.isStatus());
            stmt.setInt(7, ticket.getId());

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Parking ticket updated successfully." : "No ticket found to update.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a parking ticket
    public void delete(int id) {
        ParkingTicket ticket = findById(id);
        if (ticket != null) {
            String query = "DELETE FROM parking_ticket WHERE TicketID=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Parking ticket deleted successfully.");
                    // Update parking slot status to available (true)
                    parkingSlotService.updateSlotStatus(ticket.getParkingSlotID(), true);
                } else {
                    System.out.println("No ticket found to delete.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No ticket found with the given ID.");
        }
    }

    // Find a parking ticket by ID
    public ParkingTicket findById(int id) {
        String query = "SELECT * FROM parking_ticket WHERE TicketID=?";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ParkingTicket(
                        rs.getInt("TicketID"),
                        rs.getInt("user_id"),
                        rs.getInt("parking_id"),
                        rs.getInt("parking_slot_id"), // New addition
                        rs.getTimestamp("issuing_date"),
                        rs.getTimestamp("expiration_date"),
                        rs.getBoolean("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve all parking tickets
    public List<ParkingTicket> findAll() {
        List<ParkingTicket> tickets = new ArrayList<>();
        String query = "SELECT * FROM parking_ticket";
        try (Connection conn = dbConnection.getInstance().getConn();Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tickets.add(new ParkingTicket(
                        rs.getInt("TicketID"),
                        rs.getInt("user_id"),
                        rs.getInt("parking_id"),
                        rs.getInt("parking_slot_id"), // New addition
                        rs.getTimestamp("issuing_date"),
                        rs.getTimestamp("expiration_date"),
                        rs.getBoolean("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<ParkingTicket> findByParkingId(int parkingId) {
        List<ParkingTicket> tickets = new ArrayList<>();
        String query = "SELECT * FROM parking_ticket WHERE parking_id = ?";

        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parkingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tickets.add(new ParkingTicket(
                        rs.getInt("TicketID"),
                        rs.getInt("user_id"),
                        rs.getInt("parking_id"),
                        rs.getInt("parking_slot_id"),
                        rs.getTimestamp("issuing_date"),
                        rs.getTimestamp("expiration_date"),
                        rs.getBoolean("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public List<ParkingTicket> findByUserId(int userId) {
        List<ParkingTicket> tickets = new ArrayList<>();
        String query = "SELECT * FROM parking_ticket WHERE user_id = ?";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(new ParkingTicket(
                        rs.getInt("TicketID"),
                        rs.getInt("user_id"),
                        rs.getInt("parking_id"),
                        rs.getInt("parking_slot_id"),
                        rs.getTimestamp("issuing_date"),
                        rs.getTimestamp("expiration_date"),
                        rs.getBoolean("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }
}
