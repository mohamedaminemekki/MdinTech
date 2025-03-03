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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminBlogController {

    @FXML private VBox postsContainer;
    private final BlogServices blogService = new BlogServices();

    @FXML
    public void initialize() {
        loadPosts();
    }

    private void loadPosts() {
        try {
            postsContainer.getChildren().clear();
            List<BlogPost> posts = blogService.getAllPosts();

            if (posts.isEmpty()) {
                Label emptyLabel = new Label("Aucun article trouvé");
                emptyLabel.setStyle("-fx-text-fill: #606770; -fx-font-size: 16;");
                postsContainer.getChildren().add(emptyLabel);
                return;
            }

            posts.forEach(post -> {
                VBox postCard = createPostCard(post);
                postsContainer.getChildren().add(postCard);
            });
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les articles: " + e.getMessage());
        }
    }

    private VBox createPostCard(BlogPost post) {
        VBox card = new VBox();
        card.getStyleClass().add("post-card");
        card.setMaxWidth(800);

        // Header (Author + Date)
        HBox header = new HBox();
        header.getStyleClass().add("post-header");

        VBox authorInfo = new VBox(4);
        Label authorLabel = new Label("Auteur: " + post.getAuthor());
        authorLabel.getStyleClass().add("post-author");
        Label dateLabel = new Label(formatDate(post.getCreatedAt()));
        dateLabel.getStyleClass().add("post-date");
        authorInfo.getChildren().addAll(authorLabel, dateLabel);

        header.getChildren().add(authorInfo);

        // Title
        Label title = new Label(post.getTitle());
        title.getStyleClass().add("post-title");

        // Content
        Label content = new Label(post.getContent());
        content.getStyleClass().add("post-content");
        content.setWrapText(true);

        // Image
        HBox imageContainer = new HBox();
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(new File(post.getImageUrl()).toURI().toString()));
                imageView.setFitWidth(600);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageContainer.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Erreur de chargement d'image: " + e.getMessage());
            }
        }

        // Delete Button
        Button deleteBtn = new Button("Supprimer l'article");
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setOnAction(e -> handleDelete(post.getId()));

        card.getChildren().addAll(
                header,
                title,
                content,
                imageContainer,
                deleteBtn
        );

        return card;
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'à' HH:mm");
        return date.format(formatter);
    }

    private void handleDelete(int postId) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer cet article ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                blogService.deletePost(postId);
                showSuccessAlert("Article supprimé avec succès");
                loadPosts(); // Refresh the list
            } catch (SQLException ex) {
                showAlert("Erreur", "Échec de la suppression : " + ex.getMessage());
            }
        }
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
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