package mdinteech.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.util.Base64;

public class QRCodeMonkeyService {

    private static final String API_URL = "https://api.qrcode-monkey.com/qr/custom";

    public static void generateQRCode(String data, String outputFilePath) {
        try {
            // Construire le JSON de la requête
            String requestBody = """
            {
                "data": "%s",
                "config": {
                    "body": "square",
                    "eye": "frame0",
                    "eyeBall": "ball0",
                    "erf1": [],
                    "erf2": [],
                    "erf3": [],
                    "brf1": [],
                    "brf2": [],
                    "brf3": [],
                    "bodyColor": "#000000",
                    "bgColor": "#FFFFFF",
                    "eye1Color": "#000000",
                    "eye2Color": "#000000",
                    "eye3Color": "#000000",
                    "eyeBall1Color": "#000000",
                    "eyeBall2Color": "#000000",
                    "eyeBall3Color": "#000000",
                    "gradientColor1": "#000000",
                    "gradientColor2": "#000000",
                    "gradientType": "linear",
                    "gradientOnEyes": false,
                    "logo": ""
                },
                "size": 500,
                "download": false,
                "file": "png"
            }
            """.formatted(data);

            // Envoi de la requête HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Extraire l'URL de l'image générée
            String jsonResponse = response.body();
            String imageUrl = jsonResponse.split("\"imageUrl\":\"")[1].split("\"")[0];

            // Télécharger l'image du QR Code
            HttpRequest imageRequest = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .build();
            HttpResponse<byte[]> imageResponse = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray());

            // Sauvegarder l'image localement
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                fos.write(imageResponse.body());
            }

            System.out.println("QR Code généré et enregistré : " + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la génération du QR Code.");
        }
    }
}
