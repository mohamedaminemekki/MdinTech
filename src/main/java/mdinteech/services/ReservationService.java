package mdinteech.services;

import mdinteech.entities.Reservation;
import mdinteech.entities.Trip;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements Services<Reservation> {
    private Connection connection;

    public ReservationService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Reservation> readList() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                reservations.add(new Reservation(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("trip_id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("reservation_time"),
                        resultSet.getString("status"),
                        resultSet.getInt("seat_number"),
                        resultSet.getString("seat_type"),
                        resultSet.getString("payment_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations : " + e.getMessage());
            throw e;
        }
        return reservations;
    }

    @Override
    public void add(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservations (user_id, trip_id, transport_id, reservation_time, status, seat_number, seat_type, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, reservation.getUserId());
            statement.setInt(2, reservation.getTripId());
            statement.setInt(3, reservation.getTransportId());
            statement.setTimestamp(4, reservation.getReservationTime());
            statement.setString(5, reservation.getStatus());
            statement.setInt(6, reservation.getSeatNumber());
            statement.setString(7, reservation.getSeatType());
            statement.setString(8, reservation.getPaymentStatus());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                    System.out.println("Réservation ajoutée avec succès ! ID : " + reservation.getId());
                }
            } else {
                throw new SQLException("Erreur lors de l'ajout de la réservation.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
        String query = "UPDATE reservations SET reservation_time = ?, status = ?, seat_number = ?, seat_type = ?, payment_status = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, reservation.getReservationTime());
            statement.setString(2, reservation.getStatus());
            statement.setInt(3, reservation.getSeatNumber());
            statement.setString(4, reservation.getSeatType());
            statement.setString(5, reservation.getPaymentStatus());
            statement.setInt(6, reservation.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune réservation trouvée pour l'ID " + reservation.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la réservation : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void addP(Reservation reservation) throws SQLException {
        // Implémentation si nécessaire
    }

    @Override
    public void delete(int reservationId) throws SQLException {
        String query = "DELETE FROM reservations WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reservationId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune réservation trouvée pour l'ID " + reservationId);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réservation : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Trip getById(int id) throws SQLException {
        String query = "SELECT * FROM trips WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                LocalDate date = resultSet.getDate("date") != null ? resultSet.getDate("date").toLocalDate() : LocalDate.now();

                return new Trip(
                        resultSet.getInt("id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("departure_time"),
                        resultSet.getTimestamp("arrival_time"),
                        resultSet.getDouble("price"),
                        resultSet.getString("departure"),
                        resultSet.getString("destination"),
                        resultSet.getString("transport_name")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du trip : " + e.getMessage());
            throw e;
        }
    }

    public List<Reservation> getReservationsByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Reservation reservation = new Reservation(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("trip_id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("reservation_time"),
                        resultSet.getString("status"),
                        resultSet.getInt("seat_number"),
                        resultSet.getString("seat_type"),
                        resultSet.getString("payment_status")
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Reservation reservation = new Reservation(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("trip_id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("reservation_time"),
                        resultSet.getString("status"),
                        resultSet.getInt("seat_number"),
                        resultSet.getString("seat_type"),
                        resultSet.getString("payment_status")
                );
                reservations.add(reservation);
            }
        }

        return reservations;
    }

    public boolean isReservationExists(int tripId, int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM reservations WHERE trip_id = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tripId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Reservation> searchReservations(String keyword) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations WHERE id LIKE ? OR user_id LIKE ? OR trip_id LIKE ? OR status LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                reservations.add(new Reservation(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("trip_id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("reservation_time"),
                        resultSet.getString("status"),
                        resultSet.getInt("seat_number"),
                        resultSet.getString("seat_type"),
                        resultSet.getString("payment_status")
                ));
            }
        }
        return reservations;
    }

    public List<Reservation> searchByUserId(String keyword) throws SQLException {
        String query = "SELECT * FROM reservations WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(keyword));
            return executeQuery(statement);
        }
    }

    public List<Reservation> searchByTripId(String keyword) throws SQLException {
        String query = "SELECT * FROM reservations WHERE trip_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(keyword));
            return executeQuery(statement);
        }
    }

    public List<Reservation> searchByPaymentStatus(String keyword) throws SQLException {
        String query = "SELECT * FROM reservations WHERE payment_status LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + keyword + "%");
            return executeQuery(statement);
        }
    }

    private List<Reservation> executeQuery(PreparedStatement statement) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Reservation reservation = new Reservation(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("trip_id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("reservation_time"),
                        resultSet.getString("status"),
                        resultSet.getInt("seat_number"),
                        resultSet.getString("seat_type"),
                        resultSet.getString("payment_status")
                );
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public List<Reservation> searchByStatus(String keyword) {
        return List.of();
    }

    public List<Integer> getReservedTripIds(int userId) {
        List<Integer> tripIds = new ArrayList<>();
        String query = "SELECT trip_id FROM reservations WHERE user_id = ? AND status = 'Confirmed'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tripIds.add(rs.getInt("trip_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tripIds;
    }
}