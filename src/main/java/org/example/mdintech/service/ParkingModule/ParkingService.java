package org.example.mdintech.service.ParkingModule;

import org.example.mdintech.Singleton.dbConnection;
import org.example.mdintech.entities.ParkingModule.Parking;
import org.example.mdintech.service.Iservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingService implements Iservice<Parking> {
    private final Connection conn;

    public ParkingService() {
        this.conn = dbConnection.getInstance().getConn();
    }

    @Override
    public void save(Parking obj) {
        String query = "INSERT INTO parking (Name, Localisation, capacity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getLocalisation());
            stmt.setInt(3, obj.getCapacity());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        obj.setID(generatedKeys.getInt(1));
                        System.out.println("Parking saved with ID: " + obj.getID());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Parking obj) {
        String query = "UPDATE parking SET Name=?, Localisation=?, capacity=? WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getLocalisation());
            stmt.setInt(3, obj.getCapacity());
            stmt.setInt(4, obj.getID());

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Parking updated successfully." : "No parking found to update.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Parking obj) {
        String query = "DELETE FROM parking WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, obj.getID());
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Parking deleted successfully." : "No parking found to delete.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Parking findById(int id) {
        String query = "SELECT * FROM parking WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Parking parking = new Parking(
                        rs.getString("Name"),
                        rs.getString("Localisation"),
                        rs.getInt("capacity")
                );
                parking.setID(rs.getInt("ID"));
                return parking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Parking> findAll() {
        List<Parking> parkings = new ArrayList<>();
        String query = "SELECT * FROM parking";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Parking parking = new Parking(
                        rs.getString("Name"),
                        rs.getString("Localisation"),
                        rs.getInt("capacity")
                );
                parking.setID(rs.getInt("ID"));
                parkings.add(parking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parkings;
    }
}