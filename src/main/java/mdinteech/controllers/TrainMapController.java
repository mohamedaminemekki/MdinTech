package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.layout.AnchorPane;

public class TrainMapController {

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane mapContainer; // Assurez-vous que cet ID correspond à celui du FXML

    @FXML
    public void initialize() {
        // Charger la carte des trains depuis le fichier HTML
        String mapUrl = getClass().getResource("/mdinteech/views/train_map.html").toExternalForm();
        webView.getEngine().load(mapUrl);

        // Redimensionner la WebView pour remplir le conteneur
        webView.prefWidthProperty().bind(mapContainer.widthProperty());
        webView.prefHeightProperty().bind(mapContainer.heightProperty());
    }
}