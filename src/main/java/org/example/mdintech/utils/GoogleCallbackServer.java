package org.example.mdintech.utils;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.github.cdimascio.dotenv.Dotenv;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class GoogleCallbackServer {
    static Dotenv dotenv = Dotenv.load();
    private static final String REDIRECT_URI = "http://localhost:8080/callback";

    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/callback", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                String response = "Google authentication successful! You can close this tab.";

                if (query != null && query.contains("code=")) {
                    String code = query.split("code=")[1].split("&")[0];

                    try {
                        TokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                                new NetHttpTransport(),
                                GsonFactory.getDefaultInstance(),
                                "https://oauth2.googleapis.com/token",
                                dotenv.get("CLIENT_ID"),
                                dotenv.get("CLIENT_SECRET"),
                                code,
                                REDIRECT_URI
                        ).execute();

                        System.out.println("Access Token: " + tokenResponse.getAccessToken());

                    } catch (Exception e) {
                        response = "Error retrieving access token: " + e.getMessage();
                        e.printStackTrace();
                    }
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        });

        server.setExecutor(Executors.newFixedThreadPool(5));
        server.start();
        System.out.println("Server started at http://localhost:8080/callback");
    }
}
