package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.BlogPost;
import tn.esprit.services.BlogServices;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AdminBlogController {

    @FXML private VBox postsContainer;
    private final BlogServices blogService = new BlogServices();

    @FXML
    public void initialize() {
        loadPendingPosts();
    }

    private void loadPendingPosts() {
        try {
            postsContainer.getChildren().clear();
            for (BlogPost post : blogService.getPendingPosts()) {
                postsContainer.getChildren().add(createPostCard(post));
            }
        } catch (Exception e) {
            showAlert("Erreur", "Chargement des posts échoué");
        }
    }

    private VBox createPostCard(BlogPost post) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        // Contenu textuel
        Label content = new Label(post.getContent());
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 14px;");

        // Image du post
        HBox imageContainer = new HBox();
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            try {
                Image image = new Image(new File(post.getImageUrl()).toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setPreserveRatio(true);
                imageContainer.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Erreur de chargement d'image: " + e.getMessage());
            }
        }

        // Boutons d'action
        HBox actions = new HBox(10);
        Button approveBtn = new Button("Approuver");
        approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        approveBtn.setOnAction(e -> handleApproval(post.getId(), true)); // Ajout de l'action

        Button rejectBtn = new Button("Rejeter");
        rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        rejectBtn.setOnAction(e -> handleApproval(post.getId(), false)); // Ajout de l'action

        actions.getChildren().addAll(approveBtn, rejectBtn);

        card.getChildren().addAll(
                new Label("Auteur: " + post.getAuthor()),
                content,
                imageContainer,
                new Separator(),
                actions
        );

        return card;
    }
    private void handleApproval(int postId, boolean approved) {
        try {
            if (approved) {
                blogService.approvePost(postId);
            } else {
                blogService.rejectPost(postId);
            }
            // Rafraîchir la liste après modification
            loadPendingPosts();
        } catch (SQLException ex) {
            showAlert("Erreur", "Échec de l'opération: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void loadBlogManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/admin_blog.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) postsContainer.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Gestion du Blog");
        stage.show();
    }
}