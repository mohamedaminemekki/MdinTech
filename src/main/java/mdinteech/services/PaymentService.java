package mdinteech.services;

import mdinteech.entities.Payments;
import mdinteech.entities.Trip;
import mdinteech.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentService implements Services<Payments> {
    private Connection connection;

    public PaymentService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<Payments> readList() throws SQLException {
        List<Payments> payments = new ArrayList<>();
        String query = "SELECT * FROM payments";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Payments payment = new Payments(
                        resultSet.getInt("payment_id"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("method"),
                        resultSet.getTimestamp("payment_date"),
                        resultSet.getInt("reservation_id"),
                        resultSet.getInt("user_id")
                );
                payments.add(payment);
            }
        }
        return payments;
    }

    @Override
    public void add(Payments payment) throws SQLException {
        String query = "INSERT INTO payments (amount, method, payment_date, reservation_id, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, payment.getAmount());
            statement.setString(2, payment.getMethod());
            statement.setTimestamp(3, payment.getPaymentDate());
            statement.setInt(4, payment.getReservationId());
            statement.setInt(5, payment.getUserId());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setPaymentId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Payments payment) {
        String query = "UPDATE payments SET amount = ?, method = ?, payment_date = ?, reservation_id = ?, user_id = ? WHERE payment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, payment.getAmount());
            statement.setString(2, payment.getMethod());
            statement.setTimestamp(3, payment.getPaymentDate());
            statement.setInt(4, payment.getReservationId());
            statement.setInt(5, payment.getUserId());
            statement.setInt(6, payment.getPaymentId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addP(Payments payment) throws SQLException {
        add(payment); // Réutilisation de la méthode add
    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public Trip getById(int id) throws SQLException {
        return null;
    }
}
