package mdinteech.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HuggingFaceClient {

    private final String apiKey;  // facultatif, peut être null si vous n'en utilisez pas
    private final String model = "distilgpt2";  // modèle gratuit

    public HuggingFaceClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getChatbotResponse(String prompt) throws Exception {
        // Construire le payload JSON attendu par l'API
        JsonObject jsonPayload = new JsonObject();
        jsonPayload.addProperty("inputs", prompt);

        // Construire la requête HTTP POST
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("https://api-inference.huggingface.co/models/" + model))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString()));

        // Si vous avez un token API, ajoutez-le dans l'entête
        if (apiKey != null && !apiKey.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + apiKey);
        }
        HttpRequest request = requestBuilder.build();

        // Envoyer la requête
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parser la réponse JSON.
        // La réponse est généralement une liste d'objets avec le champ "generated_text".
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        String generatedText = jsonArray.get(0).getAsJsonObject().get("generated_text").getAsString().trim();

        return generatedText;
    }
}
