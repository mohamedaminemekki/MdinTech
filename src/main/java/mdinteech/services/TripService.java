package mdinteech.services;

import mdinteech.entities.Trip;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TripService implements Services<Trip> {

    private Connection connection;

    public TripService(Connection connection) {
        this.connection = connection;
    }

    // Vérifie si un Transport ID existe dans la table transport_types
    public boolean transportTypeExists(int transportId) throws SQLException {
        String query = "SELECT COUNT(*) FROM transport_types WHERE transport_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, transportId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    // Affiche les types de transport disponibles
    public void displayTransportTypes() throws SQLException {
        String query = "SELECT transport_id, name FROM transport_types";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            System.out.println("=== Types de transport disponibles ===");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("transport_id") + ", Nom: " + resultSet.getString("name"));
            }
        }
    }

    @Override
    public List<Trip> readList() throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT t.id, t.transport_id, t.departure_time, t.arrival_time, t.price, t.departure, t.destination, " +
                "tt.name AS transport_name, t.date " + // Ajout du champ date
                "FROM trips t " +
                "JOIN transport_types tt ON t.transport_id = tt.transport_id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date") != null ? resultSet.getDate("date").toLocalDate() : LocalDate.now(); // Récupération de la date
                Trip trip = new Trip(
                        resultSet.getInt("id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("departure_time"),
                        resultSet.getTimestamp("arrival_time"),
                        resultSet.getDouble("price"),
                        resultSet.getString("departure"),
                        resultSet.getString("destination"),
                        resultSet.getString("transport_name")
                );
                trips.add(trip);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des voyages : " + e.getMessage());
            throw e;
        }
        return trips;
    }

    @Override
    public void add(Trip trip) throws SQLException {
        // Vérifier si le Transport ID existe
        if (!transportTypeExists(trip.getTransportId())) {
            throw new SQLException("Le Transport ID " + trip.getTransportId() + " n'existe pas dans la table transport_types.");
        }

        String query = "INSERT INTO trips (transport_id, departure_time, arrival_time, price, departure, destination, transport_name, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, trip.getTransportId());
            statement.setTimestamp(2, trip.getDepartureTime());
            statement.setTimestamp(3, trip.getArrivalTime());
            statement.setDouble(4, trip.getPrice());
            statement.setString(5, trip.getDeparture());
            statement.setString(6, trip.getDestination());
            statement.setString(7, trip.getTransportName());
            statement.setDate(8, Date.valueOf(trip.getDate())); // Ajout de la date

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Erreur lors de l'ajout du voyage");
            }

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trip.setTripId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du voyage : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Trip trip) throws SQLException {
        // Vérifier si le Transport ID existe
        if (!transportTypeExists(trip.getTransportId())) {
            throw new SQLException("Le Transport ID " + trip.getTransportId() + " n'existe pas dans la table transport_types.");
        }

        String query = "UPDATE trips SET transport_id = ?, departure_time = ?, arrival_time = ?, price = ?, departure = ?, destination = ?, transport_name = ?, date = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, trip.getTransportId());
            statement.setTimestamp(2, trip.getDepartureTime());
            statement.setTimestamp(3, trip.getArrivalTime());
            statement.setDouble(4, trip.getPrice());
            statement.setString(5, trip.getDeparture());
            statement.setString(6, trip.getDestination());
            statement.setString(7, trip.getTransportName());
            statement.setDate(8, Date.valueOf(trip.getDate())); // Ajout de la date
            statement.setInt(9, trip.getTripId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun voyage trouvé avec cet ID pour la mise à jour");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du voyage : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void addP(Trip trip) throws SQLException {
        // Cette méthode semble non utilisée, elle peut être supprimée ou implémentée
    }

    @Override
    public void delete(int tripId) throws SQLException {
        String query = "DELETE FROM trips WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tripId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun voyage trouvé avec cet ID pour la suppression");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du voyage : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Trip getById(int id) throws SQLException {
        String query = "SELECT t.id, t.transport_id, t.departure_time, t.arrival_time, t.price, t.departure, t.destination, " +
                "tt.name AS transport_name, t.date " + // Ajout du champ date
                "FROM trips t " +
                "JOIN transport_types tt ON t.transport_id = tt.transport_id " +
                "WHERE t.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                LocalDate date = resultSet.getDate("date") != null ? resultSet.getDate("date").toLocalDate() : LocalDate.now(); // Récupération de la date
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
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du voyage par ID : " + e.getMessage());
            throw e;
        }
    }

    // Recherche générale (par mot-clé)
    public List<Trip> searchTrips(String keyword) throws SQLException {
        String query = "SELECT t.id, t.transport_id, t.departure_time, t.arrival_time, t.price, t.departure, t.destination, " +
                "tt.name AS transport_name, t.date " +
                "FROM trips t " +
                "JOIN transport_types tt ON t.transport_id = tt.transport_id " +
                "WHERE t.departure LIKE ? OR t.destination LIKE ? OR tt.name LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            statement.setString(3, "%" + keyword + "%");
            return executeQuery(statement);
        }
    }

    // Recherche par lieu de départ
    public List<Trip> searchByDeparture(String keyword) throws SQLException {
        String query = "SELECT t.id, t.transport_id, t.departure_time, t.arrival_time, t.price, t.departure, t.destination, " +
                "tt.name AS transport_name, t.date " +
                "FROM trips t " +
                "JOIN transport_types tt ON t.transport_id = tt.transport_id " +
                "WHERE t.departure LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + keyword + "%");
            return executeQuery(statement);
        }
    }

    // Recherche par lieu de destination
    public List<Trip> searchByDestination(String keyword) throws SQLException {
        String query = "SELECT t.id, t.transport_id, t.departure_time, t.arrival_time, t.price, t.departure, t.destination, " +
                "tt.name AS transport_name, t.date " +
                "FROM trips t " +
                "JOIN transport_types tt ON t.transport_id = tt.transport_id " +
                "WHERE t.destination LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + keyword + "%");
            return executeQuery(statement);
        }
    }

    // Recherche par Transport ID
    public List<Trip> searchByTransportId(String keyword) throws SQLException {
        String query = "SELECT t.id, t.transport_id, t.departure_time, t.arrival_time, t.price, t.departure, t.destination, " +
                "tt.name AS transport_name, t.date " +
                "FROM trips t " +
                "JOIN transport_types tt ON t.transport_id = tt.transport_id " +
                "WHERE t.transport_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(keyword));
            return executeQuery(statement);
        }
    }

    // Méthode utilitaire pour exécuter une requête et retourner une liste de trajets
    private List<Trip> executeQuery(PreparedStatement statement) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date") != null ? resultSet.getDate("date").toLocalDate() : LocalDate.now();
                Trip trip = new Trip(
                        resultSet.getInt("id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getTimestamp("departure_time"),
                        resultSet.getTimestamp("arrival_time"),
                        resultSet.getDouble("price"),
                        resultSet.getString("departure"),
                        resultSet.getString("destination"),
                        resultSet.getString("transport_name")
                );
                trips.add(trip);
            }
        }
        return trips;
    }
}