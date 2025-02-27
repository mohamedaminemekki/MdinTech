package org.example.mdintech.service.ParkingModule;

import org.example.mdintech.Singleton.dbConnection;
import org.example.mdintech.entities.ParkingModule.parkingSlot;
import org.example.mdintech.service.Iservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingSlotService implements Iservice<parkingSlot> {
    private final Connection conn;

    public ParkingSlotService() {
        this.conn = dbConnection.getInstance().getConn();
    }

    @Override
    public boolean save(parkingSlot obj) {
        String query = "INSERT INTO parking_slot (parkingID, slotName, available) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, obj.getParkingID());
            stmt.setString(2, obj.getSlotName());
            stmt.setBoolean(3, obj.isAvailable());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        obj.setSlotID(generatedKeys.getInt(1));
                        System.out.println("Parking slot saved with ID: " + obj.getSlotID());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(parkingSlot obj) {
        String query = "UPDATE parking_slot SET parkingID=?, slotName=?, available=? WHERE slotID=?";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, obj.getParkingID());
            stmt.setString(2, obj.getSlotName());
            stmt.setBoolean(3, obj.isAvailable());
            stmt.setInt(4, obj.getSlotID());

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Parking slot updated successfully." : "No parking slot found to update.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(parkingSlot obj) {
        String query = "DELETE FROM parking_slot WHERE slotID=?";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, obj.getSlotID());
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Parking slot deleted successfully." : "No parking slot found to delete.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public parkingSlot findById(int id) {
        String query = "SELECT * FROM parking_slot WHERE slotID=?";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                parkingSlot slot = new parkingSlot(
                        rs.getInt("parkingID"),
                        rs.getString("slotName")
                );
                slot.setSlotID(rs.getInt("slotID"));
                slot.setAvailable(rs.getBoolean("available"));
                return slot;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<parkingSlot> findAll() {
        List<parkingSlot> slots = new ArrayList<>();
        String query = "SELECT * FROM parking_slot";
        try (Connection conn = dbConnection.getInstance().getConn();Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                parkingSlot slot = new parkingSlot(
                        rs.getInt("parkingID"),
                        rs.getString("slotName")
                );
                slot.setSlotID(rs.getInt("slotID"));
                slot.setAvailable(rs.getBoolean("available"));
                slots.add(slot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slots;
    }

    public int getSmallestAvailableSlot(int parkingID) {
        String query = "SELECT SlotID FROM parking_slot WHERE ParkingID  = ? AND Available = 1 ORDER BY SlotID ASC LIMIT 1";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parkingID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("SlotID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public void createNewParking(int parkingId, int x) {
        String slotQuery = "INSERT INTO parking_slot (parkingID, slotName) VALUES (?, ?)";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(slotQuery)) {
            for (int i = 1; i <= x; i++) {
                stmt.setInt(1, parkingId);
                stmt.setInt(2, i);
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println(x + " parking slots created for parking ID: " + parkingId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSlotStatus(int slotID, boolean status) {
        String query = "UPDATE parking_slot SET Available = ? WHERE slotID = ?";
        try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, slotID);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Parking slot status updated successfully." : "No parking slot found to update.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
