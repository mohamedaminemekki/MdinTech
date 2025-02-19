package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {

    @FXML
    private StackPane userPane;

    @FXML
    private StackPane adminPane;

    @FXML
    private ImageView userIcon;

    @FXML
    private ImageView adminIcon;

    @FXML
    public void initialize() {
        userIcon.setImage(new Image("/images/user.jpg"));
        adminIcon.setImage(new Image("/images/admin.jpg"));
    }

    @FXML
    private void hoverEffect(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), pane);
        st.setToX(1.1);
        st.setToY(1.1);
        st.play();
    }

    @FXML
    private void removeHoverEffect(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), pane);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    @FXML
    private void goToUser(MouseEvent event) throws IOException {
        navigateTo("/AfficherReclamation.fxml");
    }

    @FXML
    private void goToAdmin(MouseEvent event) throws IOException {
        navigateTo("/AjouterReclamation.fxml");
    }

    private void navigateTo(String fxmlPath) throws IOException {
        Stage stage = (Stage) userPane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
