package mdinteech.entities;

import javafx.animation.ScaleTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Random;

public class ProgressGarden extends Pane {
    private static final Random RANDOM = new Random();
    private static final double PLANT_SPACING = 50;

    public enum PlantType {
        FLOWER("/images/plants/flower.png"),
        TREE("/images/plants/tree.png"),
        BUSH("/images/plants/bush.png");

        private final String imagePath;

        PlantType(String path) {
            this.imagePath = path;
        }
    }

    public void addPlant(PlantType type) {
        ImageView plant = new ImageView(new Image(getClass().getResourceAsStream(type.imagePath)));
        plant.setFitWidth(30);
        plant.setFitHeight(30);

        // Position aléatoire avec contrainte
        double x = RANDOM.nextDouble() * (getWidth() - PLANT_SPACING);
        double y = RANDOM.nextDouble() * (getHeight() - PLANT_SPACING);

        // Animation de croissance
        ScaleTransition scale = new ScaleTransition(Duration.millis(1000), plant);
        scale.setFromX(0.1);
        scale.setFromY(0.1);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();

        getChildren().add(plant);
    }
}