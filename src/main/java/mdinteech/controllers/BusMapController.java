package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.layout.AnchorPane;
import javafx.concurrent.Worker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BusMapController {

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane mapContainer;

    @FXML
    public void initialize() {
        // Charger la carte des bus depuis le fichier HTML
        String mapUrl = getClass().getResource("/mdinteech/views/bus_map.html").toExternalForm();
        webView.getEngine().load(mapUrl);

        // Attendre que la page soit complètement chargée
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                // Redimensionner la carte lorsque la fenêtre est redimensionnée
                webView.widthProperty().addListener((obs, oldVal, newVal) -> {
                    webView.getEngine().executeScript("map.invalidateSize()");
                });
                webView.heightProperty().addListener((obs, oldVal, newVal) -> {
                    webView.getEngine().executeScript("map.invalidateSize()");
                });

                // Mettre à jour la carte avec les données des arrêts de bus
                List<BusStop> busStops = getBusStopsFromDatabase();
                String busStopsJson = convertToJson(busStops);
                webView.getEngine().executeScript("updateBusMap(" + busStopsJson + ")");
            }
        });
    }

    private List<BusStop> getBusStopsFromDatabase() {
        List<BusStop> busStops = new ArrayList<>();
        try {
            // Connexion à la base de données
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/city_transport", "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT departure, destination FROM trips");

            // Parcourir les résultats et créer des objets BusStop
            while (resultSet.next()) {
                String departure = resultSet.getString("departure");
                String destination = resultSet.getString("destination");
                busStops.add(new BusStop(departure, destination));
            }

            // Fermer les ressources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return busStops;
    }

    private String convertToJson(List<BusStop> busStops) {
        StringBuilder json = new StringBuilder("[");
        for (BusStop stop : busStops) {
            json.append("{")
                    .append("\"departure\":\"").append(stop.getDeparture()).append("\",")
                    .append("\"destination\":\"").append(stop.getDestination()).append("\"")
                    .append("},");
        }
        if (busStops.size() > 0) {
            json.deleteCharAt(json.length() - 1); // Supprimer la dernière virgule
        }
        json.append("]");
        return json.toString();
    }

    // Classe interne pour représenter un arrêt de bus
    private static class BusStop {
        private String departure;
        private String destination;

        public BusStop(String departure, String destination) {
            this.departure = departure;
            this.destination = destination;
        }

        public String getDeparture() {
            return departure;
        }

        public String getDestination() {
            return destination;
        }
    }
}
