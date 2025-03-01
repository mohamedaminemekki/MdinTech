package mdinteech.entities;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class EnvironmentalImpactPane extends VBox {
    private static final double CO2_PER_TREE = 21.77; // kg par an
    private static final double CO2_PER_CAR = 4600; // kg par an

    private final ImageView treeIcon = new ImageView();
    private final Label impactLabel = new Label();

    public EnvironmentalImpactPane() {
        setSpacing(10);
        setAlignment(Pos.CENTER);

        treeIcon.setFitWidth(80);
        treeIcon.setFitHeight(80);
        treeIcon.setImage(new Image(getClass().getResourceAsStream("/images/tree.png")));

        impactLabel.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");

        getChildren().addAll(treeIcon, impactLabel);
    }

    public void updateImpact(int co2Saved) {
        double trees = co2Saved / CO2_PER_TREE;
        double cars = co2Saved / CO2_PER_CAR;

        // Animation
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), treeIcon);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();

        impactLabel.setText(String.format(
                "Équivalent à :\n%.1f arbres plantés\n%.2f voitures retirées",
                trees, cars
        ));
    }
}