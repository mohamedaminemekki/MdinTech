package mdinteech.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BusMapController {

    @FXML
    private AnchorPane mapContainer; // Correspond à fx:id="mapContainer" dans le FXML

    @FXML
    private WebView webView;         // Correspond à fx:id="webView"

    @FXML
    public void initialize() {
        // Charger la carte depuis le fichier HTML (assurez-vous que bus_map.html est bien dans /mdinteech/views/)
        String mapUrl = getClass().getResource("/mdinteech/views/bus_map.html").toExternalForm();
        webView.getEngine().load(mapUrl);

        // Attendre la fin du chargement de la page HTML
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // Appeler map.invalidateSize() lorsque le WebView est redimensionné
                webView.widthProperty().addListener((o, oldVal, newVal) ->
                        webView.getEngine().executeScript("map.invalidateSize()"));
                webView.heightProperty().addListener((o, oldVal, newVal) ->
                        webView.getEngine().executeScript("map.invalidateSize()"));

                // Récupérer les arrêts de bus et les afficher sur la carte
                List<BusStop> busStops = getBusStopsFromDatabase();
                String busStopsJson = convertToJson(busStops);
                webView.getEngine().executeScript("updateBusMap(" + busStopsJson + ")");
            }
        });
    }

    /**
     * Récupère les arrêts de bus depuis la base de données (table "trips" : departure, destination).
     */
    private List<BusStop> getBusStopsFromDatabase() {
        List<BusStop> busStops = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/city_transport", "root", ""
            );
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT departure, destination FROM trips");

            while (resultSet.next()) {
                String departure = resultSet.getString("departure");
                String destination = resultSet.getString("destination");
                busStops.add(new BusStop(departure, destination));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return busStops;
    }

    /**
     * Convertit la liste des arrêts de bus en JSON pour être passée à la fonction JS "updateBusMap()".
     */
    private String convertToJson(List<BusStop> busStops) {
        StringBuilder json = new StringBuilder("[");
        for (BusStop stop : busStops) {
            json.append("{")
                    .append("\"departure\":\"").append(stop.getDeparture()).append("\",")
                    .append("\"destination\":\"").append(stop.getDestination()).append("\"")
                    .append("},");
        }
        if (!busStops.isEmpty()) {
            // Supprimer la dernière virgule
            json.deleteCharAt(json.length() - 1);
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Classe interne représentant un arrêt de bus (un départ + une destination).
     */
    private static class BusStop {
        private final String departure;
        private final String destination;

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
