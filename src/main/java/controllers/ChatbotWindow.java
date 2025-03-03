package controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class ChatbotWindow extends Application {

    private TextArea chatArea;
    private TextField userInputField;
    private Button sendButton;

    @Override
    public void start(Stage stage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        userInputField = new TextField();
        sendButton = new Button("Envoyer");

        sendButton.setOnAction(e -> handleUserInput());

        VBox layout = new VBox(10, chatArea, userInputField, sendButton);
        layout.setSpacing(10);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout, 400, 400);
        stage.setTitle("Chatbot Médical");
        stage.setScene(scene);
        stage.show();
    }

    private void handleUserInput() {
        String userInput = userInputField.getText();
        if (!userInput.isEmpty()) {
            chatArea.appendText("Vous: " + userInput + "\n");
            userInputField.clear();

            // Appelez ici l'API GPT pour obtenir la réponse
            String response = getGPTResponse(userInput);
            chatArea.appendText("Bot: " + response + "\n");
        }
    }

    private HttpURLConnection createConnection(String userInput) throws IOException {
        String apiKey = ""; // Remplacez par votre clé API OpenAI
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("Clé API manquante");
        }

        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        // Préparer la chaîne JSON d'entrée
        String jsonInputString = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + userInput + "\"}],"
                + "\"max_tokens\": 150"
                + "}";

        // Écrire les données JSON dans le flux de sortie
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return connection;
    }

    private String getGPTResponse(String userInput) {
        try {
            // Appel à la méthode qui crée la connexion
            HttpURLConnection connection = createConnection(userInput);

            // Vérifier le code de réponse
            int code = connection.getResponseCode();
            if (code != 200) {
                return "Erreur : " + code;
            }

            // Lire la réponse du flux d'entrée
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Analyser la réponse JSON et extraire le texte
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("choices").getJSONObject(0).getString("message").trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur : " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
