package mdinteech.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class TripStatsController {

    @FXML private LineChart<String, Number> tripsChart;
    @FXML private PieChart destinationsChart;
    @FXML private PieChart statusChart;
    @FXML private Label totalTripsLabel;
    @FXML private Label revenueLabel;
    @FXML private Label occupancyLabel;
    @FXML private BarChart<String, Number> profitableTripsChart;
    @FXML private BarChart<String, Number> unprofitableTripsChart;
    @FXML private LineChart<String, Number> cancellationHeatMap;
    @FXML private PieChart destinationCancellationChart;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private TripService tripService;
    private ReservationService reservationService;

    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            tripService = new TripService(connection);
            reservationService = new ReservationService(connection);

            loadGeneralStats();
            setupTripsChart();
            setupDestinationsChart();
            setupStatusChart();
            setupOccupancy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGeneralStats() throws SQLException {
        totalTripsLabel.setText(String.valueOf(tripService.getTotalTrips()));
        revenueLabel.setText(String.format("%.2f DT", reservationService.getMonthlyRevenue()));
    }

    private void setupTripsChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Trajets par mois");

        Map<String, Integer> tripsData = tripService.getTripsPerMonth();
        tripsData.forEach((month, count) ->
                series.getData().add(new XYChart.Data<>(month, count)));

        tripsChart.getData().add(series);
    }

    private void setupDestinationsChart() throws SQLException {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<String, Integer> destinationsData = tripService.getPopularDestinations(5);
        destinationsData.forEach((dest, count) ->
                pieData.add(new PieChart.Data(dest + " (" + count + ")", count)));
        destinationsChart.setData(pieData);
    }

    private void setupStatusChart() throws SQLException {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<String, Integer> statusData = reservationService.getReservationStatusStats();
        statusData.forEach((status, count) -> {
            String formattedStatus = status.substring(0, 1).toUpperCase() + status.substring(1);
            pieData.add(new PieChart.Data(formattedStatus + " (" + count + ")", count));
        });
        statusChart.setData(pieData);
    }

    private void setupOccupancy() throws SQLException {
        double occupancyRate = reservationService.getAverageOccupancy();
        occupancyLabel.setText(String.format("%.1f%%", occupancyRate));
    }
    private void setupProfitabilityAnalysis() throws SQLException {
        // Top 5 rentables
        XYChart.Series<String, Number> profitableSeries = new XYChart.Series<>();
        profitableSeries.setName("Revenu total");
        Map<String, Double> profitableData = reservationService.getMostProfitableTrips(5);
        profitableData.forEach((trip, revenue) ->
                profitableSeries.getData().add(new XYChart.Data<>(trip, revenue)));
        profitableTripsChart.getData().add(profitableSeries);

        // Top 5 peu rentables
        XYChart.Series<String, Number> unprofitableSeries = new XYChart.Series<>();
        unprofitableSeries.setName("Revenu total");
        Map<String, Double> unprofitableData = reservationService.getLeastProfitableTrips(5);
        unprofitableData.forEach((trip, revenue) ->
                unprofitableSeries.getData().add(new XYChart.Data<>(trip, revenue)));
        unprofitableTripsChart.getData().add(unprofitableSeries);
    }

    private void setupCancellationAnalysis() throws SQLException {
        // Carte thermique des annulations
        XYChart.Series<String, Number> cancellationSeries = new XYChart.Series<>();
        cancellationSeries.setName("Taux d'annulation");
        Map<String, Double> cancellationData = reservationService.getCancellationByHour();
        cancellationData.forEach((hour, rate) ->
                cancellationSeries.getData().add(new XYChart.Data<>(hour, rate)));
        cancellationHeatMap.getData().add(cancellationSeries);

        // Annulations par destination
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<String, Integer> destinationData = reservationService.getCancellationByDestination();
        destinationData.forEach((dest, count) ->
                pieData.add(new PieChart.Data(dest + " (" + count + ")", count)));
        destinationCancellationChart.setData(pieData);
    }

    private void setupTimeFilters() {
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void updateTimeAnalysis() {
        try {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            // Rafraîchir les données avec les nouveaux filtres
            refreshCancellationData(start, end);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCancellationData(LocalDate start, LocalDate end) throws SQLException {
        // Rafraîchir les graphiques avec les nouvelles dates
        Map<String, Double> newCancellationData = reservationService.getCancellationByHour(start, end);
        Map<String, Integer> newDestinationData = reservationService.getCancellationByDestination(start, end);

        // Mettre à jour les graphiques...
    }
}