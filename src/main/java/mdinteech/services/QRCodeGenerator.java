package mdinteech.services;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class QRCodeGenerator {

    // Méthode pour générer un QR code à partir d'une URL
    public static void generateQRCode(String ticketUrl) {
        try {
            // URL de l'API GoQR.me pour générer un QR code
            String qrCodeApiUrl = "http://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + URLEncoder.encode(ticketUrl, StandardCharsets.UTF_8.toString());

            // Télécharger l'image du QR code
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(qrCodeApiUrl))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                // Afficher le QR code dans une nouvelle fenêtre
                Image qrCodeImage = new Image(new ByteArrayInputStream(response.body()));
                Stage qrStage = new Stage();
                qrStage.setTitle("QR Code de Réservation");

                ImageView imageView = new ImageView(qrCodeImage);
                VBox vbox = new VBox(imageView);
                vbox.setAlignment(Pos.CENTER);
                vbox.setPadding(new Insets(20));

                Scene scene = new Scene(vbox, 250, 250);
                qrStage.setScene(scene);
                qrStage.show();
            } else {
                System.err.println("Erreur lors de la génération du QR code : " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}