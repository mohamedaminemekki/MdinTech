package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import tn.esprit.entities.Reclamation;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ReclamationServices;
import tn.esprit.services.ReponseServices;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherReclamationClient {

    @FXML
    private ListView<Reclamation> listViewReclamations;

    @FXML
    private Button btnActualiser, btnSearch;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField clientIdField;

    private final ReclamationServices reclamationService = new ReclamationServices();
    private final ReponseServices reponseService = new ReponseServices();
    private List<Reclamation> allReclamations = new ArrayList<>(); // Store all reclamations

    @FXML
    private void initialize() {
        btnActualiser.setOnAction(event -> chargerReclamations());
        btnSearch.setOnAction(event -> rechercherReclamations());

        // Initial load of reclamations based on client ID
        chargerReclamations();

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

                    // Define column constraints
                    ColumnConstraints col1 = new ColumnConstraints();
                    col1.setPercentWidth(10); // ID
                    ColumnConstraints col2 = new ColumnConstraints();
                    col2.setPercentWidth(20); // Client ID
                    ColumnConstraints col3 = new ColumnConstraints();
                    col3.setPercentWidth(20); // Type
                    ColumnConstraints col4 = new ColumnConstraints();
                    col4.setPercentWidth(20); // Date
                    ColumnConstraints col5 = new ColumnConstraints();
                    col5.setPercentWidth(15); // État
                    ColumnConstraints col6 = new ColumnConstraints();
                    col6.setPercentWidth(15); // Réponse
                    ColumnConstraints col7 = new ColumnConstraints();
                    col7.setPercentWidth(10); // Action

                    gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);

                    // Create Labels for each column
                    Label idLabel = createLabel(String.valueOf(rec.getId()), "-fx-font-weight: bold; -fx-text-fill: #333;");
                    Label clientLabel = createLabel(String.valueOf(rec.getClient_id()), "-fx-text-fill: #555;");
                    Label typeLabel = createLabel(rec.getType(), "-fx-font-style: italic; -fx-text-fill: #666;");
                    Label dateLabel = createLabel(rec.getDatee(), "-fx-text-fill: #777;");
                    Label etatLabel = createLabel(rec.getState() ? "Traité" : "Non traité", rec.getState() ? "-fx-text-fill: green; -fx-font-weight: bold;" : "-fx-text-fill: red; -fx-font-weight: bold;");

                    // Add response handling
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

                    String reponseTexte = (reponse != null) ? reponse.getMessage() : "Pas de réponse";
                    if (reponseTexte.length() > 40) {
                        reponseTexte = reponseTexte.substring(0, 40) + "...";
                    }
                    Label reponseLabel = createLabel(reponseTexte, "-fx-text-fill: black; -fx-cursor: hand; -fx-underline: true;");

                    // Modify Button
                    Button modifyButton = new Button();
                    modifyButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                    ImageView modifyIcon = new ImageView("/images/modify.png");
                    modifyIcon.setFitWidth(20);
                    modifyIcon.setFitHeight(20);
                    modifyIcon.setPreserveRatio(true);
                    modifyButton.setGraphic(modifyIcon);

                    modifyButton.setOnAction(e -> {
                        if (!rec.getState()) { // If "Non traité"
                            openModifyDialog(rec); // Open modify dialog for this reclamation
                        } else {
                            afficherAlerte("Erreur", "La réclamation est déjà traitée.");
                        }
                    });

                    // Delete Button
                    Button deleteButton = new Button();
                    deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                    ImageView deleteIcon = new ImageView("/images/delete.png"); // Path to your delete icon
                    deleteIcon.setFitWidth(20);
                    deleteIcon.setFitHeight(20);
                    deleteIcon.setPreserveRatio(true);
                    deleteButton.setGraphic(deleteIcon);

                    deleteButton.setOnAction(e -> {
                        if (!rec.getState()) { // Si "Non traité"
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation de suppression");
                            alert.setHeaderText("Supprimer la réclamation ?");
                            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ? Cette action est irréversible.");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                try {
                                    reclamationService.delete(rec.getId()); // Appel de la méthode delete
                                    listViewReclamations.getItems().remove(rec); // Suppression de l'élément de la liste
                                    afficherAlerte("Succès", "Réclamation supprimée avec succès.");
                                } catch (SQLException ex) {
                                    System.err.println("Erreur lors de la suppression de la réclamation : " + ex.getMessage());
                                    afficherAlerte("Erreur", "Échec de la suppression de la réclamation.");
                                }
                            }
                        } else {
                            afficherAlerte("Erreur", "La réclamation est déjà traitée et ne peut pas être supprimée.");
                        }
                    });

                    // Add elements to the GridPane
                    gridPane.add(idLabel, 0, 0);
                    gridPane.add(clientLabel, 1, 0);
                    gridPane.add(typeLabel, 2, 0);
                    gridPane.add(dateLabel, 3, 0);
                    gridPane.add(etatLabel, 4, 0);
                    gridPane.add(reponseLabel, 5, 0);
                    gridPane.add(modifyButton, 6, 0); // Modify button in column 6
                    gridPane.add(deleteButton, 6, 1); // Puts delete button right below modify button to fit inside the cell


                    // Bind GridPane width to ListView width
                    gridPane.prefWidthProperty().bind(listViewReclamations.widthProperty().subtract(20)); // Adjust for padding

                    setGraphic(gridPane);

                    // Set on click behavior for response field
                    Reponse finalReponse = reponse;
                    reponseLabel.setOnMouseClicked(e -> {
                        if (finalReponse != null) {
                            descriptionArea.setText(finalReponse.getMessage()); // Display the response in the description area
                        } else {
                            descriptionArea.setText("Aucune réponse pour cette réclamation.");
                        }
                    });
                }
            }
        });

        listViewReclamations.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                Reclamation selectedReclamation = listViewReclamations.getSelectionModel().getSelectedItem();
                if (selectedReclamation != null) {
                    afficherDescription(selectedReclamation);
                }
            }
        });
    }

    @FXML
    private void openModifyDialog(Reclamation reclamation) {
        // Create dialog to modify the description of the reclamation
        TextInputDialog descriptionDialog = new TextInputDialog(reclamation.getDescription());
        descriptionDialog.setTitle("Modifier la réclamation");
        descriptionDialog.setHeaderText("Modifier la description de la réclamation");

        descriptionDialog.showAndWait().ifPresent(newDescription -> {
            reclamation.setDescription(newDescription);

            // Create a ChoiceDialog to modify the type of the reclamation
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>(
                    reclamation.getType(),
                    "Problème d'application",
                    "Réclamation service administratif",
                    "Réclamation service de transport",
                    "Réclamation service hospitalier",
                    "Réclamation service supermarché en ligne",
                    "Autre problème"
            );
            typeDialog.setTitle("Modifier le type de réclamation");
            typeDialog.setHeaderText("Modifier le type de réclamation");

            // Show the dialog and handle the selection
            typeDialog.showAndWait().ifPresent(newType -> {
                reclamation.setType(newType);

                // Update the reclamation in the database
                try {
                    reclamationService.update(reclamation);
                    afficherAlerte("Succès", "Réclamation mise à jour avec succès !");
                    chargerReclamations(); // Refresh the list
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Erreur lors de la mise à jour de la réclamation: " + e.getMessage());
                }
            });
        });
    }


    @FXML
    private void chargerReclamations() {
        try {
            allReclamations = reclamationService.readList();
            String clientIdText = clientIdField.getText().trim();

            if (!clientIdText.isEmpty()) {
                // Filter by client ID if entered
                int clientId = Integer.parseInt(clientIdText);
                List<Reclamation> filteredReclamations = allReclamations.stream()
                        .filter(rec -> rec.getClient_id() == clientId)
                        .collect(Collectors.toList());
                listViewReclamations.getItems().setAll(filteredReclamations);
            } else {
                listViewReclamations.getItems().setAll(allReclamations); // Display all if no filter
            }

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les réclamations : " + e.getMessage());
        }
    }

    @FXML
    private void rechercherReclamations() {
        String clientIdText = clientIdField.getText().trim();

        if (clientIdText.isEmpty()) {
            chargerReclamations(); // Reset if empty
            return;
        }

        try {
            int clientId = Integer.parseInt(clientIdText);
            List<Reclamation> filteredReclamations = allReclamations.stream()
                    .filter(rec -> rec.getClient_id() == clientId)
                    .collect(Collectors.toList());

            listViewReclamations.getItems().setAll(filteredReclamations);
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Client ID invalide.");
        }
    }

    private void afficherDescription(Reclamation reclamation) {
        descriptionArea.setText(reclamation.getDescription());
    }

    private Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    private void afficherAlerte(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
