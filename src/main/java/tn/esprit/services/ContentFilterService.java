package tn.esprit.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContentFilterService {

    // Exemple d'URL : https://www.purgomalum.com/service/json?text=Votre+texte
    public String filterText(String text) throws Exception {
        // Encoder le texte pour l'URL
        String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
        String requestUrl = "https://www.purgomalum.com/service/json?text=" + encodedText;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return response.toString(); // Le JSON renvoyé contient le texte filtré dans "result"
    }

    // Méthode utilitaire pour vérifier si le texte est modifié (donc potentiellement inapproprié)
    public boolean isTextToxic(String originalText, String filteredJson) {
        // Le JSON renvoyé par Purgomalum ressemble à {"result":"Texte filtré"}
        // Si le texte filtré est différent du texte original, c'est qu'il y avait des mots à filtrer.
        // On peut donc considérer le texte comme potentiellement toxique.
        try {
            org.json.JSONObject json = new org.json.JSONObject(filteredJson);
            String filteredText = json.getString("result");
            return !filteredText.equalsIgnoreCase(originalText);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
