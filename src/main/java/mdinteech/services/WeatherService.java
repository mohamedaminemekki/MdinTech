package mdinteech.services;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherService {

    private static final String API_KEY = "d452905258bc890b5f654b9a041081c7";

    public static JSONObject getWeatherData(String cityName) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + ",TN&appid=" + API_KEY;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) { // Vérifier que la réponse est OK
                System.out.println("Erreur HTTP " + responseCode + " lors de la récupération de la météo pour " + cityName);
                if (responseCode == 404) {
                    System.out.println("Ville non trouvée : " + cityName + ". Essai avec les coordonnées GPS...");
                    JSONObject coordinates = getCoordinates(cityName);
                    if (coordinates != null) {
                        double lat = coordinates.getDouble("lat");
                        double lon = coordinates.getDouble("lon");
                        return getWeatherByCoordinates(lat, lon);
                    }
                }
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return new JSONObject(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWeatherIconUrl(String iconCode) {
        if (iconCode == null || iconCode.isEmpty()) {
            return null; // Retourne null si le code d'icône est invalide
        }
        return "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
    }

    public static JSONObject getCoordinates(String cityName) {
        try {
            String urlString = "http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + ",TN&limit=1&appid=" + API_KEY;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Erreur HTTP " + responseCode + " lors de la récupération des coordonnées.");
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Affichage de la réponse JSON pour debug
            System.out.println("Réponse JSON de l'API Geo : " + response.toString());

            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                return jsonArray.getJSONObject(0); // Prendre le premier résultat
            } else {
                System.out.println("Aucune donnée de coordonnées trouvée pour : " + cityName);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getWeatherByCoordinates(double lat, double lon) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Erreur HTTP " + responseCode + " lors de la récupération de la météo par coordonnées.");
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return new JSONObject(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
