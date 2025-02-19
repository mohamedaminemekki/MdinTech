    package controllers;

    import javafx.collections.FXCollections;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.*;
    import javafx.stage.Stage;
    import tn.esprit.entities.Reclamation;
    import tn.esprit.entities.Reponse;
    import tn.esprit.services.ReclamationServices;
    import tn.esprit.services.ReponseServices;

    import java.io.IOException;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.stream.Collectors;

    public class AfficherReclamation {

        @FXML
        private ListView<Reclamation> listViewReclamations;

        @FXML
        private Button btnActualiser, btnSearch;

        @FXML
        private TextArea descriptionArea;

        @FXML
        private ComboBox<String> chercherPar;

        @FXML
        private TextField searchField;

        private final ReclamationServices reclamationService = new ReclamationServices();
        private final ReponseServices reponseService = new ReponseServices();
        private List<Reclamation> allReclamations = new ArrayList<>(); // Store all reclamations

        @FXML
        private void initialize() {
            VBox.setVgrow(descriptionArea, Priority.NEVER); // Prevent it from stretching
            chercherPar.setItems(FXCollections.observableArrayList("Client ID", "Type", "Date", "État"));
            chercherPar.setPromptText("Veuillez sélectionner le type de recherche");
            chargerReclamations();
            btnActualiser.setOnAction(event -> chargerReclamations());
            btnSearch.setOnAction(event -> rechercherReclamations());

            final boolean[] isReponseClicked = {false}; // Variable pour gérer l'état de clic

            listViewReclamations.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Reclamation rec, boolean empty) {
                    super.updateItem(rec, empty);
                    if (empty || rec == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        GridPane gridPane = new GridPane();
                        gridPane.setHgap(10);
                        gridPane.setVgap(5);
                        gridPane.setStyle("-fx-padding: 10px; -fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5px;");
                        gridPane.setAlignment(Pos.CENTER_LEFT);

                        // Define column constraints (match with header GridPane)
                        ColumnConstraints col1 = new ColumnConstraints();
                        col1.setPercentWidth(5); // ID
                        ColumnConstraints col2 = new ColumnConstraints();
                        col2.setPercentWidth(10); // Client ID
                        ColumnConstraints col3 = new ColumnConstraints();
                        col3.setPercentWidth(15); // Type
                        ColumnConstraints col4 = new ColumnConstraints();
                        col4.setPercentWidth(15); // Date
                        ColumnConstraints col5 = new ColumnConstraints();
                        col5.setPercentWidth(10); // État
                        ColumnConstraints col6 = new ColumnConstraints();
                        col6.setPercentWidth(30); // Réponse
                        ColumnConstraints col7 = new ColumnConstraints();
                        col7.setPercentWidth(10); // Action

                        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);

                        // Create Labels for each column
                        Label idLabel = createLabel(String.valueOf(rec.getId()), "-fx-font-weight: bold; -fx-text-fill: #333;");
                        Label clientLabel = createLabel(String.valueOf(rec.getClient_id()), "-fx-text-fill: #555;");
                        Label typeLabel = createLabel(rec.getType(), "-fx-font-style: italic; -fx-text-fill: #666;");
                        Label dateLabel = createLabel(rec.getDatee(), "-fx-text-fill: #777;");
                        Label etatLabel = createLabel(rec.getState() ? "Traité" : "Non traité", rec.getState() ? "-fx-text-fill: green; -fx-font-weight: bold;" : "-fx-text-fill: red; -fx-font-weight: bold;");
                        etatLabel.setOnMouseClicked(event -> toggleState(rec, etatLabel));

                        // Load action icons
                        ImageView deleteIcon = new ImageView(new Image("/images/delete.png"));
                        deleteIcon.setFitWidth(20);
                        deleteIcon.setFitHeight(20);
                        deleteIcon.setOnMouseClicked(event -> supprimerReclamation(rec));

                        ImageView repondreIcon = new ImageView(new Image("/images/reply.png"));
                        repondreIcon.setFitWidth(20);
                        repondreIcon.setFitHeight(20);
                        repondreIcon.setOnMouseClicked(event -> ouvrirFenetreReponse(rec));

                        HBox actionsBox = new HBox(10);
                        actionsBox.getChildren().addAll(deleteIcon, repondreIcon);

                        // Fetch response for the reclamation
                        Reponse reponse = null;
                        try {
                            List<Reponse> reponses = reponseService.readList();
                            for (Reponse rep : reponses) {
                                if (rep.getReclamationId() == rec.getId()) {
                                    reponse = rep;
                                    break;
                                }
                            }
                        } catch (SQLException e) {
                            System.err.println("Erreur lors de la récupération des réponses : " + e.getMessage());
                        }

                        // Display response text
                        String reponseTexte = (reponse != null) ? reponse.getMessage() : "Pas de réponse";
                        if (reponseTexte.length() > 40) {
                            reponseTexte = reponseTexte.substring(0, 40) + "...";
                        }
                        Label reponseLabel = createLabel(reponseTexte, "-fx-text-fill: black; -fx-cursor: hand; -fx-underline: true;");

                        Reponse finalReponse = reponse;
                        reponseLabel.setOnMouseClicked(event -> {
                            if (finalReponse != null) {
                                descriptionArea.setText(finalReponse.getMessage());
                                isReponseClicked[0] = true;
                            } else {
                                descriptionArea.setText("Aucune réponse disponible.");
                                isReponseClicked[0] = true;
                            }
                            event.consume(); // Prevent click propagation
                        });
                        reponseLabel.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 1) {
                                // Un seul clic : afficher la réponse dans la zone de texte existante
                                if (finalReponse != null) {
                                    descriptionArea.setText(finalReponse.getMessage());
                                    isReponseClicked[0] = true;
                                } else {
                                    descriptionArea.setText("Aucune réponse disponible.");
                                    isReponseClicked[0] = true;
                                }
                                event.consume();
                            } else if (event.getClickCount() == 2) {
                                // Double clic : ouvrir une fenêtre de modification
                                if (finalReponse != null) {
                                    ouvrirFenetreModification(finalReponse);
                                }
                            }
                        });

                        // Add elements to the GridPane
                        gridPane.add(idLabel, 0, 0);
                        gridPane.add(clientLabel, 1, 0);
                        gridPane.add(typeLabel, 2, 0);
                        gridPane.add(dateLabel, 3, 0);
                        gridPane.add(etatLabel, 4, 0);
                        gridPane.add(reponseLabel, 5, 0);
                        gridPane.add(actionsBox, 6, 0);

                        // Bind GridPane width to ListView width
                        gridPane.prefWidthProperty().bind(listViewReclamations.widthProperty().subtract(20)); // Adjust for padding

                        setGraphic(gridPane);
                    }
                }
            });

            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteResponseItem = new MenuItem("Supprimer la réponse");
            deleteResponseItem.setOnAction(event -> {
                Reclamation selectedReclamation = listViewReclamations.getSelectionModel().getSelectedItem();
                if (selectedReclamation != null) {
                    try {
                        List<Reponse> reponses = reponseService.readList();
                        for (Reponse rep : reponses) {
                            if (rep.getReclamationId() == selectedReclamation.getId()) {
                                supprimerReponse(rep);
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println("Erreur lors de la récupération des réponses : " + e.getMessage());
                    }
                }
            });
            contextMenu.getItems().add(deleteResponseItem);

            listViewReclamations.setContextMenu(contextMenu);

            listViewReclamations.setOnMouseClicked(event -> {
                if (!isReponseClicked[0]) { // Vérifier si un clic sur la réponse a été effectué
                    Reclamation selectedReclamation = listViewReclamations.getSelectionModel().getSelectedItem();
                    if (selectedReclamation != null) {
                        descriptionArea.setText(selectedReclamation.getDescription());
                    }
                }
                isReponseClicked[0] = false; // Réinitialiser après le clic
            });

        }


        private void ouvrirFenetreModification(Reponse finalReponse) {
            Stage stage = new Stage();
            stage.setTitle("Modifier la Réponse");

            VBox root = new VBox(10);
            root.setPadding(new Insets(10));

            Label label = new Label("Modifier la réponse:");
            TextArea textArea = new TextArea(finalReponse.getMessage());
            textArea.setWrapText(true);

            Button saveButton = new Button("Enregistrer");
            saveButton.setOnAction(e -> {
                try {
                    finalReponse.setMessage(textArea.getText()); // Update the response message
                    reponseService.update(finalReponse); // Update the response in the database

                    // Refresh the ListView to reflect the changes
                    listViewReclamations.refresh();

                    stage.close(); // Close the window
                } catch (SQLException ex) {
                    System.err.println("Erreur lors de la mise à jour : " + ex.getMessage());
                }
            });

            root.getChildren().addAll(label, textArea, saveButton);
            Scene scene = new Scene(root, 300, 200);
            stage.setScene(scene);
            stage.show();
        }

        private void ouvrirFenetreReponse(Reclamation rec) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReponse.fxml"));
                Parent root = loader.load();

                AjouterReponse controller = loader.getController();
                controller.setReclamationId(rec.getId());

                Stage stage = new Stage();
                stage.setTitle("Répondre à la Réclamation");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                afficherAlerte("Erreur", "Impossible d'ouvrir la fenêtre de réponse: " + e.getMessage());
            }
        }


        private Label createLabel(String text, String style) {
            Label label = new Label(text);
            label.setStyle(style);
            return label;
        }

        private void chargerReclamations() {
            try {
                allReclamations = reclamationService.readList();
                listViewReclamations.getItems().setAll(allReclamations);
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de charger les réclamations : " + e.getMessage());
            }
        }

        private void rechercherReclamations() {
            String critere = chercherPar.getValue();
            String valeur = searchField.getText().trim().toLowerCase();

            if (critere == null || valeur.isEmpty()) {
                listViewReclamations.getItems().setAll(allReclamations); // Reset if empty
                return;
            }

            List<Reclamation> filteredReclamations = allReclamations.stream()
                    .filter(rec -> switch (critere) {
                        case "Client ID" -> String.valueOf(rec.getClient_id()).contains(valeur);
                        case "Type" -> rec.getType().toLowerCase().contains(valeur);
                        case "Date" -> rec.getDatee().toLowerCase().contains(valeur);
                        case "État" -> rec.getState() ? "traité".contains(valeur) : "non traité".contains(valeur);
                        default -> true;
                    })
                    .collect(Collectors.toList());

            listViewReclamations.getItems().setAll(filteredReclamations);
        }

        private void toggleState(Reclamation rec, Label etatLabel) {
            boolean newState = !rec.getState();
            rec.setState(newState);
            etatLabel.setText(newState ? "Traité" : "Non traité");
            etatLabel.setStyle(newState ? "-fx-text-fill: green; -fx-font-weight: bold;" : "-fx-text-fill: red; -fx-font-weight: bold;");

            try {
                reclamationService.update(rec);
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de mettre à jour l'état de la réclamation : " + e.getMessage());
            }
        }

        private void supprimerReclamation(Reclamation rec) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmer la suppression");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        reclamationService.delete(rec.getId());
                        listViewReclamations.getItems().remove(rec);
                    } catch (SQLException e) {
                        afficherAlerte("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                    }
                }
            });
        }
        private void supprimerReponse(Reponse reponse) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmer la suppression");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette réponse ?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        reponseService.delete(reponse.getId()); // Delete the response from the database
                        chargerReclamations(); // Refresh the ListView to reflect the changes
                    } catch (SQLException e) {
                        afficherAlerte("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                    }
                }
            });
        }
        private void afficherAlerte(String titre, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }