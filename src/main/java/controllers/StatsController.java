package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import services.RendezVousServices;

import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

public class StatsController {

    @FXML
    private BarChart<String, Number> barChart;

    private final RendezVousServices rendezVousServices = new RendezVousServices();

    @FXML
    public void initialize() {
        try {
            Map<String, Integer> stats = rendezVousServices.getRendezVousStats();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Rendez-vous");

            Random rand = new Random();  // Instancie un générateur de nombres aléatoires

            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
                series.getData().add(data);

                // Génère une couleur aléatoire en format hexadécimal
                String randomColor = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

                // Attendre que le nœud de la barre soit créé pour appliquer le style
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        Platform.runLater(() -> newNode.setStyle("-fx-bar-fill: " + randomColor + ";"));
                    }
                });
            }

            barChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
