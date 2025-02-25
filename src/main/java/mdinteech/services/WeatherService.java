package mdinteech.services;

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

            // Vérifier le code de réponse HTTP
            int responseCode = conn.getResponseCode();
            if (responseCode == 404) {
                System.err.println("Ville non trouvée : " + cityName);
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
}