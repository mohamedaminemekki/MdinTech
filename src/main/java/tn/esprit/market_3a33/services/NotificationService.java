package tn.esprit.market_3a33.services;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationService {

    /**
     * Simulates sending a notification to an API.
     *
     * @param message The notification message to send.
     */
    public static void showNotification(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle("Order Confirmed");
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }
    public static void sendNotification(String message) {
        try {
            // Simulate an API call
            URL url = new URL("https://api.example.com/notifications");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send the notification message
            String jsonInputString = "{\"message\": \"" + message + "\"}";
            try (var outputStream = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Notification sent successfully: " + message);
            } else {
                System.out.println("Failed to send notification. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}