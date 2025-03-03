package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.BlogPost;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.User;
import tn.esprit.services.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class UserBlogController {

    @FXML private VBox feedContainer;
    @FXML private TextArea postContent;
    @FXML private Label fileNameLabel;
    @FXML private TextField postTitle;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ImageView userAvatar;
    @FXML private ImageView imagePreview;
    @FXML private Button generatePostTextButton;
    @FXML private TextField searchField;

    private String imagePath;
    private final BlogServices blogService = new BlogServices();
    private final CommentServices commentService = new CommentServices();
    private final LikeServices likeService = new LikeServices();

    @FXML
    public void initialize() {
        loadUserAvatar();
        setupCategoryCombo();
        loadPosts();
        generatePostTextButton.setOnAction(e -> handleGeneratePostText());
    }

    private void handleGeneratePostText() {
        String title = postTitle.getText();
        if (title == null || title.trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un titre pour générer le texte du post.");
            return;
        }
        generatePostTextButton.setText("Génération en cours...");
        generatePostTextButton.setDisable(true);
        new Thread(() -> {
            String generatedText = PostTextGeneratorServices.generatePostText(title);
            javafx.application.Platform.runLater(() -> {
                if (generatedText != null && !generatedText.isEmpty()) {
                    postContent.setText(generatedText);
                } else {
                    showAlert("Erreur", "Impossible de générer le texte du post. Veuillez réessayer.");
                }
                generatePostTextButton.setText("Générer le texte du post");
                generatePostTextButton.setDisable(false);
            });
        }).start();
    }

    private void loadUserAvatar() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
            try {
                Image avatar = new Image(new File(currentUser.getAvatarUrl()).toURI().toString());
                userAvatar.setImage(avatar);
            } catch (Exception e) {
                loadDefaultAvatar();
            }
        } else {
            loadDefaultAvatar();
        }
        // Appliquer un masque circulaire
        Circle clip = new Circle(userAvatar.getFitWidth() / 2, userAvatar.getFitHeight() / 2, userAvatar.getFitWidth() / 2);
        userAvatar.setClip(clip);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadPosts();
            return;
        }
        try {
            feedContainer.getChildren().clear();
            List<BlogPost> allPosts = blogService.getApprovedPosts();
            for (BlogPost post : allPosts) {
                if (post.getTitle() != null && post.getTitle().toLowerCase().contains(keyword)) {
                    feedContainer.getChildren().add(createPostCard(post));
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de rechercher les posts : " + e.getMessage());
        }
    }

    private void loadDefaultAvatar() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/logo_plus.png");
            if (is != null) {
                Image image = new Image(is);
                userAvatar.setImage(image);
            } else {
                System.err.println("Avatar par défaut non trouvé !");
                Image backupImage = new Image(new File("/images/logo_plus.png").toURI().toString());
                userAvatar.setImage(backupImage);
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'avatar : " + e.getMessage());
        }
    }

    private void setupCategoryCombo() {
        categoryCombo.getItems().addAll("Actualité", "Événement", "Astuce", "Question", "Autre");
    }

    @FXML
    private void handleAddImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) uploadDir.mkdir();
                File dest = new File(uploadDir, System.currentTimeMillis() + "_" + file.getName());
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = dest.getAbsolutePath();
                imagePreview.setImage(new Image(dest.toURI().toString()));
                imagePreview.setVisible(true);
                imagePreview.setManaged(true);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de charger l'image");
            }
        }
    }

    @FXML
    private void handleNewPost() {
        if (postTitle.getText().isEmpty() || postContent.getText().isEmpty()) {
            showAlert("Champs requis", "Le titre et le contenu sont obligatoires");
            return;
        }
        BlogPost post = new BlogPost();
        post.setAuthorAvatarUrl(SessionManager.getCurrentUser().getAvatarUrl());
        post.setTitle(postTitle.getText());
        post.setContent(postContent.getText());
        post.setAuthor(SessionManager.getCurrentUser().getNom());
        post.setAuthor_cin(SessionManager.getCurrentUser().getCin());
        post.setImageUrl(imagePath);
        post.setCategory(categoryCombo.getValue());
        post.setCreatedAt(LocalDateTime.now());
        try {
            blogService.createPost(post);
            clearForm();
            feedContainer.getChildren().add(0, createPostCard(post));
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la publication");
        }
    }

    private void clearForm() {
        postTitle.clear();
        postContent.clear();
        categoryCombo.getSelectionModel().clearSelection();
        imagePreview.setImage(null);
        imagePreview.setVisible(false);
        imagePreview.setManaged(false);
        imagePath = null;
    }

    private void loadPosts() {
        try {
            System.out.println("Tentative de chargement des posts...");
            feedContainer.getChildren().clear();
            List<BlogPost> posts = blogService.getApprovedPosts();
            if (posts.isEmpty()) {
                feedContainer.getChildren().add(new Label("Aucun post disponible 😞"));
                return;
            }
            posts.forEach(post -> {
                System.out.println("Post chargé - ID: " + post.getId());
                feedContainer.getChildren().add(createPostCard(post));
            });
        } catch (SQLException e) {
            System.err.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur BDD", e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur générale: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Problème de chargement : " + e.getMessage());
        }
    }

    private VBox createPostCard(BlogPost post) {
        VBox card = new VBox(10);
        card.getStyleClass().add("post-card");
        card.setMaxWidth(600);
        card.setUserData(post.getId());

        // ---------------------------
        // Header
        // ---------------------------
        HBox header = new HBox(10);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = new ImageView();
        try {
            Image avatarImage = new Image(new File(post.getAuthorAvatarUrl()).toURI().toString());
            avatar.setImage(avatarImage);
        } catch (Exception e) {
            avatar.setImage(new Image("https://fr.vecteezy.com/art-vectoriel/1840618-image-profil-icon-male-icon-human-or-people-sign-and-symbol-vector"));
        }
        avatar.setFitHeight(40);
        avatar.setFitWidth(40);
        Circle clip = new Circle(20, 20, 20);
        avatar.setClip(clip);

        VBox userInfo = new VBox(2);
        userInfo.getStyleClass().add("author-info");
        Label authorName = new Label(post.getAuthor());
        authorName.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label postTime = new Label(formatDateTime(post.getCreatedAt()));
        postTime.setStyle("-fx-text-fill: #606770; -fx-font-size: 12;");
        userInfo.getChildren().addAll(authorName, postTime);

        // Bouton options si l'utilisateur est l'auteur
        Node optionsButton = null;
        if (SessionManager.getCurrentUser().getCin().equals(post.getAuthorCin())) {
            Button btnOptions = new Button("⋮");
            btnOptions.getStyleClass().add("options-button");
            ContextMenu contextMenu = new ContextMenu();
            MenuItem modifyItem = new MenuItem("Modifier");
            modifyItem.setOnAction(e -> showEditPostDialog(post));
            MenuItem deleteItem = new MenuItem("Supprimer");
            deleteItem.setOnAction(e -> handleDeletePost(post));
            contextMenu.getItems().addAll(modifyItem, deleteItem);
            btnOptions.setOnAction(e -> contextMenu.show(btnOptions, Side.BOTTOM, 0, 0));
            optionsButton = btnOptions;
        }

        HBox headerLeft = new HBox(10, avatar, userInfo);
        header.getChildren().add(headerLeft);
        if (optionsButton != null) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            header.getChildren().addAll(spacer, optionsButton);
        }

        // ---------------------------
        // Titre et Contenu
        // ---------------------------
        Label title = new Label(post.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        Label content = new Label(post.getContent());
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 14; -fx-text-fill: #050505;");

        // ---------------------------
        // Boutons d'interaction
        // ---------------------------
        HBox interactions = new HBox(15);
        Button likeBtn = new Button("❤ " + post.getLikeCount());
        likeBtn.getStyleClass().add("interaction-btn");
        likeBtn.setOnAction(e -> handleLike(post));
        Tooltip likeTooltip = new Tooltip();
        likeBtn.setTooltip(likeTooltip);
        likeBtn.hoverProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                try {
                    List<User> likedUsers = likeService.getUsersWhoLikedPost(post.getId());
                    StringBuilder sb = new StringBuilder();
                    if (likedUsers.isEmpty()) {
                        sb.append("Aucun like");
                    } else {
                        for (User u : likedUsers) {
                            sb.append(u.getNom()).append("\n");
                        }
                    }
                    likeTooltip.setText(sb.toString());
                } catch (Exception ex) {
                    likeTooltip.setText("Erreur de récupération");
                }
            }
        });
        // Clic droit pour afficher la liste des utilisateurs ayant liké
        likeBtn.setOnMouseClicked(e -> {
            if (e.isSecondaryButtonDown()) {
                try {
                    List<User> likedUsers = likeService.getUsersWhoLikedPost(post.getId());
                    ContextMenu usersMenu = new ContextMenu();
                    for (User u : likedUsers) {
                        HBox userItem = new HBox(5);
                        ImageView userAvatar = new ImageView();
                        try {
                            userAvatar.setImage(new Image(new File(u.getAvatarUrl()).toURI().toString()));
                        } catch (Exception ex) {
                            userAvatar.setImage(new Image("https://fr.vecteezy.com/art-vectoriel/1840618-image-profil-icon-male-icon-human-or-people-sign-and-symbol-vector"));
                        }
                        userAvatar.setFitWidth(20);
                        userAvatar.setFitHeight(20);
                        Label nameLabel = new Label(u.getNom());
                        userItem.getChildren().addAll(userAvatar, nameLabel);
                        CustomMenuItem menuItem = new CustomMenuItem(userItem, false);
                        usersMenu.getItems().add(menuItem);
                    }
                    usersMenu.show(likeBtn, Side.TOP, 0, 0);
                } catch (Exception ex) {
                    showAlert("Erreur", "Impossible de récupérer la liste des likes");
                }
            }
        });
        Button commentBtn = new Button("💬 " + post.getCommentCount());
        commentBtn.getStyleClass().add("interaction-btn");

        // ---------------------------
        // Zone de saisie inline pour commentaire
        // ---------------------------
        HBox commentInputContainer = new HBox(10);
        commentInputContainer.setAlignment(Pos.CENTER_LEFT);
        commentInputContainer.setVisible(false);
        commentInputContainer.setManaged(false);
        TextField newCommentField = new TextField();
        newCommentField.setPromptText("Écrire un commentaire...");
        Button publishCommentBtn = new Button("Publier");
        publishCommentBtn.setOnAction(ev -> {
            String commentText = newCommentField.getText().trim();
            if (!commentText.isEmpty()) {
                addCommentIfAppropriate(post, commentText);
                newCommentField.clear();
            }
        });

        commentInputContainer.getChildren().addAll(newCommentField, publishCommentBtn);

        // Le bouton "commenter" bascule l'affichage de la zone de saisie inline
        commentBtn.setOnAction(e -> {
            commentInputContainer.setVisible(!commentInputContainer.isVisible());
            commentInputContainer.setManaged(!commentInputContainer.isManaged());
        });

        interactions.getChildren().addAll(likeBtn, commentBtn);

        // ---------------------------
        // Affichage de l'image associée
        // ---------------------------
        Node imageNode = createImageContainer(post.getImageUrl());

        // ---------------------------
        // Conteneur des commentaires existants
        // ---------------------------
        VBox commentsContainer = new VBox(5);
        commentsContainer.getStyleClass().add("comments-container");
        if (post.getComments() != null) {
            for (tn.esprit.entities.Comment comment : post.getComments()) {
                commentsContainer.getChildren().add(createCommentCard(comment));
            }
        }

        // Assemblage final de la carte
        card.getChildren().addAll(header, title, content, imageNode, new Separator(), interactions, commentInputContainer, commentsContainer);
        return card;
    }

    private void handleDeletePost(BlogPost post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le post");
        alert.setContentText("Voulez-vous vraiment supprimer ce post ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                blogService.deletePost(post.getId());
                loadPosts();
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer le post : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showEditPostDialog(BlogPost post) {
        Dialog<BlogPost> dialog = new Dialog<>();
        dialog.setTitle("Modifier le post");
        TextField titleField = new TextField(post.getTitle());
        TextArea contentArea = new TextArea(post.getContent());
        TextField categoryField = new TextField(post.getCategory());
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new Label("Titre:"), titleField, new Label("Contenu:"), contentArea, new Label("Catégorie:"), categoryField);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                post.setTitle(titleField.getText());
                post.setContent(contentArea.getText());
                post.setCategory(categoryField.getText());
                return post;
            }
            return null;
        });
        Optional<BlogPost> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                blogService.updatePost(post);
                refreshPost(post.getId());
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de modifier le post : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM HH:mm");
        return dateTime.format(formatter);
    }

    private Node createImageContainer(String imageUrl) {
        HBox container = new HBox();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(new File(imageUrl).toURI().toString()));
                imageView.setFitWidth(500);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                container.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Erreur de chargement d'image: " + e.getMessage());
            }
        }
        return container;
    }

    private void handleLike(BlogPost post) {
        try {
            likeService.toggleLike(SessionManager.getCurrentUser().getCin(), post.getId());
            refreshPost(post.getId());
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'action : " + e.getMessage());
        }
    }

    private void refreshPost(int postId) {
        try {
            BlogPost updatedPost = blogService.getPostById(postId);
            feedContainer.getChildren().replaceAll(node -> {
                if (node.getUserData() != null && (int) node.getUserData() == postId) {
                    return createPostCard(updatedPost);
                }
                return node;
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showCommentDialog(BlogPost post) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Nouveau commentaire");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Écrivez votre commentaire...");
        commentArea.setWrapText(true);
        dialog.getDialogPane().setContent(commentArea);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return commentArea.getText();
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(comment -> {
            try {
                commentService.addComment(post.getId(), SessionManager.getCurrentUser().getCin(), comment);
                refreshPost(post.getId());
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'ajouter le commentaire");
            }
        });
    }
    private void addCommentIfAppropriate(BlogPost post, String commentText) {
        ContentFilterService filterService = new ContentFilterService();
        try {
            String filteredJson = filterService.filterText(commentText);
            if (filterService.isTextToxic(commentText, filteredJson)) {
                showAlert("Commentaire refusé", "Votre commentaire contient des termes inappropriés.");
            } else {
                commentService.addComment(post.getId(), SessionManager.getCurrentUser().getCin(), commentText);
                refreshPost(post.getId());
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de vérifier le commentaire pour contenu inapproprié.");
            e.printStackTrace();
        }
    }


    private VBox createCommentCard(tn.esprit.entities.Comment comment) {
        VBox commentBox = new VBox(5);
        commentBox.getStyleClass().add("comment-box");

        // Header du commentaire
        HBox header = new HBox(10);
        Label authorLabel = new Label(comment.getAuthorCin());
        authorLabel.setStyle("-fx-font-weight: bold;");
        Label timeLabel = new Label(formatDateTime(comment.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #606770; -fx-font-size: 10;");
        header.getChildren().addAll(authorLabel, timeLabel);

        // Contenu en mode lecture
        Label contentLabel = new Label(comment.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 12;");

        // Zone d'édition inline (initialement masquée)
        HBox editContainer = new HBox(10);
        editContainer.setAlignment(Pos.CENTER_LEFT);
        editContainer.setVisible(false);
        editContainer.setManaged(false);
        TextField editField = new TextField(comment.getContent());
        Button saveEditBtn = new Button("Valider");
        Button cancelEditBtn = new Button("Annuler");
        editContainer.getChildren().addAll(editField, saveEditBtn, cancelEditBtn);

        saveEditBtn.setOnAction(e -> {
            String newContent = editField.getText().trim();
            if (!newContent.isEmpty()) {
                try {
                    commentService.updateComment(comment.getId(), newContent);
                    comment.setContent(newContent);
                    contentLabel.setText(newContent);
                    editContainer.setVisible(false);
                    editContainer.setManaged(false);
                    contentLabel.setVisible(true);
                    contentLabel.setManaged(true);
                } catch (SQLException ex) {
                    showAlert("Erreur", "Impossible de modifier le commentaire");
                    ex.printStackTrace();
                }
            }
        });

        cancelEditBtn.setOnAction(e -> {
            editField.setText(comment.getContent());
            editContainer.setVisible(false);
            editContainer.setManaged(false);
            contentLabel.setVisible(true);
            contentLabel.setManaged(true);
        });

        // Actions du commentaire
        HBox actions = new HBox(10);
        Button editBtn = new Button("Modifier");
        editBtn.setOnAction(e -> {
            contentLabel.setVisible(false);
            contentLabel.setManaged(false);
            editContainer.setVisible(true);
            editContainer.setManaged(true);
        });
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setOnAction(e -> handleDeleteComment(comment));
        actions.getChildren().addAll(editBtn, deleteBtn);

        commentBox.getChildren().addAll(header, contentLabel, editContainer, actions);
        return commentBox;
    }

    private void showEditCommentDialog(tn.esprit.entities.Comment comment) {
        // Optionnel si vous souhaitez aussi ouvrir une fenêtre de dialogue,
        // mais ici l'édition inline est déjà gérée dans createCommentCard.
    }

    private void handleDeleteComment(tn.esprit.entities.Comment comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le commentaire ?");
        alert.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                commentService.deleteComment(comment.getId());
                refreshPost(comment.getPostId());
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer le commentaire");
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void loadBlogPosts() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/blog_posts.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) feedContainer.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Blog Communautaire");
        stage.show();
    }
}
