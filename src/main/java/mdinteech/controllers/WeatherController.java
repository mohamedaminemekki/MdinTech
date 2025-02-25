package mdinteech.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;

public class WeatherController {

    @FXML
    private Label departureCityLabel;
    @FXML
    private Label departureTempLabel; // Nouveau Label

    @FXML
    private Label destinationTempLabel; // Nouveau Label
    @FXML
    private Label departureWeatherLabel;

    @FXML
    private Label departureHumidityLabel;

    @FXML
    private Label departureWindLabel;

    @FXML
    private Label departurePressureLabel;

    @FXML
    private ImageView departureWeatherIcon;

    @FXML
    private Label destinationCityLabel;

    @FXML
    private Label destinationWeatherLabel;

    @FXML
    private Label destinationHumidityLabel;

    @FXML
    private Label destinationWindLabel;

    @FXML
    private Label destinationPressureLabel;

    @FXML
    private ImageView destinationWeatherIcon;

    @FXML
    private Label weatherMessage;

    @FXML
    private LineChart<Number, Number> temperatureChart;

    public void setWeather(String departureCity, String departureWeather, String departureIconUrl,
                           double departureTempKelvin, double departureHumidity, double departureWindSpeed, double departurePressure,
                           String destinationCity, String destinationWeather, String destinationIconUrl,
                           double destinationTempKelvin, double destinationHumidity, double destinationWindSpeed, double destinationPressure) {
        // Convertir la température de Kelvin en Celsius
        double departureTempCelsius = departureTempKelvin - 273.15;
        double destinationTempCelsius = destinationTempKelvin - 273.15;

        // Météo pour la ville de départ
        departureCityLabel.setText("Départ: " + departureCity);
        departureWeatherLabel.setText(departureWeather);
        departureTempLabel.setText(String.format("Température: %.1f °C", departureTempCelsius)); // Afficher la température
        departureHumidityLabel.setText("Humidité: " + departureHumidity + "%");
        departureWindLabel.setText("Vent: " + departureWindSpeed + " m/s");
        departurePressureLabel.setText("Pression: " + departurePressure + " hPa");
        departureWeatherIcon.setImage(departureIconUrl != null ? new Image(departureIconUrl) : new Image("/images/default_icon.png"));

        // Météo pour la ville de destination
        destinationCityLabel.setText("Destination: " + destinationCity);
        destinationWeatherLabel.setText(destinationWeather);
        destinationTempLabel.setText(String.format("Température: %.1f °C", destinationTempCelsius)); // Afficher la température
        destinationHumidityLabel.setText("Humidité: " + destinationHumidity + "%");
        destinationWindLabel.setText("Vent: " + destinationWindSpeed + " m/s");
        destinationPressureLabel.setText("Pression: " + destinationPressure + " hPa");
        destinationWeatherIcon.setImage(destinationIconUrl != null ? new Image(destinationIconUrl) : new Image("/images/default_icon.png"));

        // Générer un message personnalisé
        String message = generateWeatherMessage(departureWeather, destinationWeather);
        weatherMessage.setText(message);

        // Animer les icônes météo
        animateWeatherIcon(departureWeatherIcon);
        animateWeatherIcon(destinationWeatherIcon);

        // Charger le graphique de température
        loadTemperatureChart();
    }
    private void loadTemperatureChart() {
        // Créer une série de données
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Température (°C)");

        // Ajouter des données simulées
        series.getData().add(new XYChart.Data<>(0, 20)); // Minuit
        series.getData().add(new XYChart.Data<>(6, 18)); // 6h
        series.getData().add(new XYChart.Data<>(12, 25)); // Midi
        series.getData().add(new XYChart.Data<>(18, 22)); // 18h
        series.getData().add(new XYChart.Data<>(24, 19)); // Minuit

        // Ajouter la série au graphique
        temperatureChart.getData().add(series);

        // Personnaliser l'apparence du graphique
        temperatureChart.setCreateSymbols(true);
        temperatureChart.setAnimated(true);
        temperatureChart.setLegendVisible(false);
    }

    private void animateWeatherIcon(ImageView weatherIcon) {
        // Animation de fondu
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), weatherIcon);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();

        // Animation de zoom
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1.5), weatherIcon);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    private String generateWeatherMessage(String departureWeather, String destinationWeather) {
        if (isGoodWeather(departureWeather) && isGoodWeather(destinationWeather)) {
            return " 🌞 La météo est parfaite pour voyager ! Profitez de votre trajet en toute sécurité. 🌞";
        } else if (isBadWeather(departureWeather) || isBadWeather(destinationWeather)) {
            return "⚠️ Attention ! Les conditions météo ne sont pas idéales. Soyez prudent lors de votre voyage. ⚠️";
        } else {
            return "🌤️ La météo est acceptable, mais restez vigilant. Bon voyage ! 🌤️";
        }
    }

    private boolean isGoodWeather(String weatherDescription) {
        return weatherDescription.toLowerCase().contains("clear") || weatherDescription.toLowerCase().contains("sunny");
    }

    private boolean isBadWeather(String weatherDescription) {
        return weatherDescription.toLowerCase().contains("rain") || weatherDescription.toLowerCase().contains("storm") || weatherDescription.toLowerCase().contains("snow");
    }
}