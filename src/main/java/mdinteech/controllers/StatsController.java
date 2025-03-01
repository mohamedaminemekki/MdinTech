package mdinteech.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import mdinteech.entities.Badge;
import mdinteech.entities.MysteryReward;
import mdinteech.services.ReservationService;
import mdinteech.services.TripService;
import mdinteech.utils.DatabaseConnection;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class StatsController {

    @FXML private VBox rootContainer;
    @FXML private Label loyaltyPointsLabel;
    @FXML private FlowPane badgesContainer;
    @FXML private VBox challengesContainer;
    @FXML private Label tripsCountLabel;
    @FXML private Label favoriteTransportLabel;
    @FXML private Label totalDistanceLabel;
    @FXML private Label co2SavedLabel;
    @FXML private LineChart<String, Number> monthlyActivityChart;
    @FXML private PieChart transportPieChart;
    @FXML private VBox transportDistributionBox;
    @FXML private Label currentStreakLabel;
    @FXML private ComboBox<String> periodFilterCombo;
    @FXML private Button darkModeToggleButton;
    @FXML private ImageView modeIcon;
    @FXML private FlowPane gardenContainer;
    @FXML private Label envImpactLabel;

    private boolean darkMode = false;

    private final ReservationService reservationService;
    private final TripService tripService;
    private final int currentUserId = 9;

    // Récompense mystère : par exemple, un bon de réduction après 10 trajets
    private final MysteryReward mysteryReward = new MysteryReward("reward1", "Bon de réduction", 10);

    public StatsController() throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        this.reservationService = new ReservationService(connection);
        this.tripService = new TripService(connection);
    }

    @FXML
    public void initialize() throws SQLException {
        periodFilterCombo.getItems().addAll("Ce mois", "Ce trimestre", "Cette année");
        periodFilterCombo.setValue("Ce mois");
        periodFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyPeriodFilter(newVal));

        darkModeToggleButton.setOnAction(e -> toggleDarkMode());

        calculateStats();
    }

    private void calculateStats() throws SQLException {
        List<Integer> tripIds = reservationService.getReservedTripIds(currentUserId);
        Map<String, List<LocalDate>> transportUsage = new HashMap<>();
        Map<Month, Integer> monthlyTrips = new EnumMap<>(Month.class);
        double totalDistance = 0;
        int totalCO2 = 0;

        for (Integer tripId : tripIds) {
            String transportType = tripService.getTransportType(tripId);
            LocalDate tripDate = tripService.getTripDate(tripId);
            double distance = tripService.getTripDistance(tripId);

            transportUsage.computeIfAbsent(transportType, k -> new ArrayList<>()).add(tripDate);
            monthlyTrips.merge(tripDate.getMonth(), 1, Integer::sum);

            totalDistance += distance;
            totalCO2 += calculateCO2Savings(transportType, distance);
        }

        updateUI(tripIds.size(), transportUsage, monthlyTrips, totalDistance, totalCO2);
    }

    private void updateUI(int totalTrips, Map<String, List<LocalDate>> transportUsage,
                          Map<Month, Integer> monthlyTrips, double totalDistance, int totalCO2) {

        loyaltyPointsLabel.setText((totalTrips * 10) + " points");
        tripsCountLabel.setText("Total des trajets : " + totalTrips);
        favoriteTransportLabel.setText("Transport préféré : " + getFavoriteTransport(transportUsage));
        totalDistanceLabel.setText(String.format("Distance totale : %.1f km", totalDistance));
        co2SavedLabel.setText("CO₂ économisé : " + totalCO2 + " kg");

        updateTransportDistribution(transportUsage);
        setupMonthlyActivityChart(monthlyTrips);
        updateStreakStats(transportUsage);
        updateGamificationElements(totalTrips, transportUsage, totalDistance);

        // Mise à jour des nouvelles fonctionnalités
        updateDynamicBadges(totalDistance, getTotalTrips(transportUsage), transportUsage);
        updateGardenProgress(getTotalTrips(transportUsage));
        updateEnvironmentalImpact(totalCO2);
        checkMysteryReward(getTotalTrips(transportUsage));
    }

    // ----------------------- Méthodes d'affichage classiques -----------------------

    private void addBadge(String badgeId, String description) {
        // Chemin = /images/badges/ + badgeId + .png
        InputStream is = getClass().getResourceAsStream("/images/badges/" + badgeId + ".png");
        if (is == null) {
            System.err.println("Badge non trouvé : " + badgeId);
            return;
        }
        Image badgeImg = new Image(is);

        VBox badgeContainer = new VBox(5);
        badgeContainer.setAlignment(Pos.CENTER);

        ImageView badgeImage = new ImageView(badgeImg);
        badgeImage.setFitWidth(80);
        badgeImage.setFitHeight(80);

        Label badgeLabel = new Label(description);
        badgeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #636e72;");

        badgeContainer.getChildren().addAll(badgeImage, badgeLabel);
        badgesContainer.getChildren().add(badgeContainer);

        // Animation au survol
        badgeContainer.setOnMouseEntered(e -> {
            badgeContainer.setScaleX(1.1);
            badgeContainer.setScaleY(1.1);
        });
        badgeContainer.setOnMouseExited(e -> {
            badgeContainer.setScaleX(1.0);
            badgeContainer.setScaleY(1.0);
        });
    }

    // Amélioration de l'affichage des défis avec explications et pourcentage
    private void addChallenge(String challengeText, double progress) {
        VBox challengeBox = new VBox(8);
        challengeBox.setStyle("-fx-background-color: rgba(230,230,230,0.6); -fx-background-radius: 8; -fx-padding: 10;");

        Label challengeLabel = new Label("• " + challengeText);
        challengeLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");

        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.getStyleClass().add("challenge-progress");
        progressBar.setPrefWidth(250);

        int percentage = (int)(progress * 100);
        Label progressLabel = new Label("Progression : " + percentage + "%");
        progressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2d3436;");

        challengeBox.getChildren().addAll(challengeLabel, progressBar, progressLabel);
        challengesContainer.getChildren().add(challengeBox);
    }

    private int getMaxStreak(Map<String, List<LocalDate>> transportUsage) {
        List<LocalDate> allDates = transportUsage.values().stream()
                .flatMap(List::stream)
                .sorted()
                .toList();

        int maxStreak = 0;
        int currentStreak = 1;
        for (int i = 1; i < allDates.size(); i++) {
            if (ChronoUnit.DAYS.between(allDates.get(i - 1), allDates.get(i)) == 1) {
                currentStreak++;
            } else {
                maxStreak = Math.max(maxStreak, currentStreak);
                currentStreak = 1;
            }
        }
        return Math.max(maxStreak, currentStreak);
    }

    private void updateStreakStats(Map<String, List<LocalDate>> transportUsage) {
        List<LocalDate> allDates = transportUsage.values().stream()
                .flatMap(List::stream)
                .sorted()
                .toList();

        int currentStreak = calculateCurrentStreak(allDates);
        currentStreakLabel.setText("Série actuelle : " + currentStreak + " jours");
        if (currentStreak >= 3)
            addBadge("streak_" + currentStreak, "Série de " + currentStreak + " jours !");
    }

    private int calculateCurrentStreak(List<LocalDate> dates) {
        if (dates.isEmpty()) return 0;
        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate lastDate = dates.get(dates.size() - 1);
        while (!lastDate.isBefore(today.minusDays(streak))) {
            streak++;
            if (!dates.contains(today.minusDays(streak - 1)))
                break;
        }
        return streak;
    }

    private void updateGamificationElements(int totalTrips,
                                            Map<String, List<LocalDate>> transportUsage,
                                            double totalDistance) {

        // Réinitialiser les badges et défis
        badgesContainer.getChildren().clear();
        challengesContainer.getChildren().clear();

        if (totalTrips >= 1)
            addBadge("first_trip", "Premier trajet réalisé");
        if (totalDistance >= 100)
            addBadge("distance_100", "100 km parcourus");
        if (transportUsage.size() >= 3)
            addBadge("explorer", "Explorateur : 3 types de transports");
        if (checkNightTrips(transportUsage))
            addBadge("night_rider", "Voyageur nocturne");

        addChallenge("Atteindre 1000 km (" + (int) totalDistance + "/1000)", totalDistance >= 1000 ? 1.0 : totalDistance / 1000.0);
        addChallenge("Utiliser 5 transports (" + transportUsage.size() + "/5)", transportUsage.size() >= 5 ? 1.0 : (double) transportUsage.size() / 5.0);
        addChallenge("Série de 7 jours (" + getMaxStreak(transportUsage) + "/7)", getMaxStreak(transportUsage) >= 7 ? 1.0 : (double) getMaxStreak(transportUsage) / 7.0);
    }

    // ----------------------- Nouvelles fonctionnalités -----------------------

    // 1. Badges dynamiques évolutifs
    private void updateDynamicBadges(double totalDistance, int totalTrips, Map<String, List<LocalDate>> transportUsage) {
        // Pour le badge "arbre évolutif" : chaque 100 km augmente le niveau
        int treeLevel = (int) (totalDistance / 100);
        if (treeLevel > 0) {
            addBadge("tree_badge_level" + treeLevel, "Votre arbre a atteint le niveau " + treeLevel);
        }
        // Badge "Explorateur Urbain" débloqué après 5 modes de transport
        if (transportUsage.size() >= 5) {
            addBadge("explorateur_urbain", "Explorateur Urbain : 5 modes de transport utilisés");
        }
    }

    // 2. Jardin de progression : chaque trajet plante une nouvelle image (fleur/arbre)
    private void updateGardenProgress(int totalTrips) {
        gardenContainer.getChildren().clear();
        for (int i = 0; i < totalTrips; i++) {
            InputStream is = getClass().getResourceAsStream("/images/plants/tree3.jpg");
            if (is == null) {
                System.err.println("Image de plante introuvable : /images/plants/tree3.jpg");
                return;
            }
            ImageView plant = new ImageView(new Image(is));
            plant.setFitWidth(50);
            plant.setFitHeight(50);
            Tooltip tooltip = new Tooltip("Arbre n°" + (i + 1) + " planté !");
            Tooltip.install(plant, tooltip);
            gardenContainer.getChildren().add(plant);
        }
    }

    // 3. Impact environnemental ludique
    private void updateEnvironmentalImpact(int totalCO2) {
        // Exemple : 100 kg de CO₂ économisé équivalent à 1 arbre sauvé
        int treesSaved = totalCO2 / 100;
        envImpactLabel.setText("Vous avez sauvé l'équivalent de " + treesSaved + " arbres ce mois-ci !");
    }

    // 4. Récompense mystère
    private void checkMysteryReward(int totalTrips) {
        // Si le nombre de trajets est un multiple de 10 et la récompense est éligible
        if (totalTrips % 10 == 0 && totalTrips > 0 && mysteryReward.isEligible(totalTrips)) {
            mysteryReward.award();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Récompense Mystère");
            alert.setHeaderText(null);
            alert.setContentText("Félicitations ! Vous débloquez une récompense mystère : " + mysteryReward.getRewardType());
            alert.showAndWait();
        }
    }

    // ----------------------- Méthodes utilitaires -----------------------

    private boolean checkNightTrips(Map<String, List<LocalDate>> transportUsage) {
        return transportUsage.values().stream()
                .flatMap(List::stream)
                .anyMatch(date -> {
                    try {
                        int tripId = tripService.getTripIdByDate(date);
                        int hour = tripService.getTripHour(tripId);
                        return hour >= 22 || hour <= 6;
                    } catch (SQLException e) {
                        return false;
                    }
                });
    }

    private int calculateCO2Savings(String transportType, double distance) {
        Map<String, Integer> co2Factors = Map.of(
                "metro", 35,
                "bus", 45,
                "train", 25,
                "voiture", 120
        );
        return (int) (distance * co2Factors.getOrDefault(transportType.toLowerCase(), 0));
    }

    private String getFavoriteTransport(Map<String, List<LocalDate>> transportUsage) {
        return transportUsage.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse("Aucun");
    }

    private int getTotalTrips(Map<String, List<LocalDate>> transportUsage) {
        return transportUsage.values().stream().mapToInt(List::size).sum();
    }

    private void updateTransportDistribution(Map<String, List<LocalDate>> transportUsage) {
        transportDistributionBox.getChildren().clear();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        transportUsage.forEach((transport, dates) -> {
            pieChartData.add(new PieChart.Data(transport, dates.size()));
            Label label = new Label(String.format("%s : %d (%.0f%%)",
                    transport,
                    dates.size(),
                    (dates.size() * 100.0) / getTotalTrips(transportUsage)));
            label.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
            transportDistributionBox.getChildren().add(label);
        });
        transportPieChart.setData(pieChartData);
    }

    private void setupMonthlyActivityChart(Map<Month, Integer> monthlyTrips) {
        monthlyActivityChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Activité Mensuelle");

        for (Month month : Month.values()) {
            String monthName = month.getDisplayName(TextStyle.SHORT, Locale.FRENCH);
            int count = monthlyTrips.getOrDefault(month, 0);
            XYChart.Data<String, Number> data = new XYChart.Data<>(monthName, count);
            data.setNode(new HoveredNode(count));
            series.getData().add(data);
        }
        monthlyActivityChart.getData().add(series);
        monthlyActivityChart.setLegendVisible(false);
        monthlyActivityChart.setAnimated(true);
    }

    private static class HoveredNode extends Circle {
        HoveredNode(int value) {
            super(5, Color.web("#3498db"));
            setEffect(new DropShadow(5, Color.web("#3498db77")));
            Tooltip tooltip = new Tooltip("Trajets : " + value);
            Tooltip.install(this, tooltip);
        }
    }

    private void applyPeriodFilter(String period) {
        try {
            calculateStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleDarkMode() {
        darkMode = !darkMode;
        if (darkMode) {
            rootContainer.getStyleClass().add("dark-mode");
            InputStream moonStream = getClass().getResourceAsStream("/images/moon.png");
            if (moonStream != null)
                modeIcon.setImage(new Image(moonStream));
        } else {
            rootContainer.getStyleClass().remove("dark-mode");
            InputStream sunStream = getClass().getResourceAsStream("/images/sun.png");
            if (sunStream != null)
                modeIcon.setImage(new Image(sunStream));
        }
    }
}
