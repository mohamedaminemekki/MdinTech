package tn.esprit.services;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PostTextGeneratorServices {

    private static final String HF_API_TOKEN = "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";
    private static final String MODEL_NAME = "mistralai/Mistral-7B-Instruct-v0.2"; // Modèle optimisé

    public static String generatePostText(String title) {
        try {
            URL url = new URL("https://api-inference.huggingface.co/models/" + MODEL_NAME);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + HF_API_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Prompt amélioré
            String prompt = "[INST] Génère un post LinkedIn professionnel en français avec ce titre : '"
                    + title
                    + "'. Structure avec des paragraphes courts et des emojis. [/INST]";

            JSONObject payload = new JSONObject();
            payload.put("inputs", prompt);
            payload.put("parameters", new JSONObject()
                    .put("max_new_tokens", 300)
                    .put("temperature", 0.8));

            // Envoyer la requête
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }

            // Gérer le temps de chargement du modèle
            if (conn.getResponseCode() == 503) { // Modèle en cours de chargement
                Thread.sleep(10000); // Attendre 10 secondes
                return generatePostText(title); // Réessayer
            }

            // Lire la réponse
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray response = new JSONArray(reader.readLine());
            return response.getJSONObject(0).getString("generated_text");

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur : " + e.getMessage();
        }
    }
}