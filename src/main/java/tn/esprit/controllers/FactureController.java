package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Facture;
import tn.esprit.services.FactureServices;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FactureController {

    @FXML private ListView<Facture> factureList;
    private final FactureServices factureService = new FactureServices();
    private final ObservableList<Facture> factureData = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initializeAdminData() {
        configureListCells();
        refreshList();
        insertAddButtonAboveList();
        addColumnHeaders();
    }


    private void addColumnHeaders() {
        GridPane header = new GridPane();
        ColumnConstraints[] constraints = createColumnConstraints();
        header.getColumnConstraints().addAll(constraints);
        header.setHgap(15);
        header.setVgap(5);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        String[] headers = {
                "CIN Client", "Type Facture", "Date Facturation",
                "Date Échéance", "État", "Montant (TND)", "Actions"
        };

        for (int i = 0; i < headers.length; i++) {
            Label headerLabel = new Label(headers[i]);
            headerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            header.add(headerLabel, i, 0);
        }

        // Insert header in the container that holds the factureList.
        Parent parent = factureList.getParent();
        if (parent instanceof Pane) {
            Pane container = (Pane) parent;
            int listIndex = container.getChildren().indexOf(factureList);
            if (listIndex >= 0) {
                container.getChildren().add(listIndex, header);
            }
            // Optionally adjust for AnchorPane.
            if (container instanceof AnchorPane) {
                AnchorPane anchorPane = (AnchorPane) container;
                AnchorPane.setTopAnchor(header, 60.0);
                AnchorPane.setLeftAnchor(header, 0.0);
                AnchorPane.setRightAnchor(header, 0.0);
                AnchorPane.setTopAnchor(factureList, 100.0);
            }
        }
    }

    private ColumnConstraints[] createColumnConstraints() {
        double[] widths = {120, 120, 120, 120, 80, 100, 80}; // For 7 columns.
        ColumnConstraints[] constraints = new ColumnConstraints[widths.length];
        for (int i = 0; i < widths.length; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(widths[i]);
            cc.setMinWidth(widths[i]);
            cc.setMaxWidth(widths[i]);
            constraints[i] = cc;
        }
        return constraints;
    }

    private void configureListCells() {
        factureList.setCellFactory(new Callback<ListView<Facture>, ListCell<Facture>>() {
            @Override
            public ListCell<Facture> call(ListView<Facture> param) {
                return new ListCell<Facture>() {
                    private final Label cinLabel = new Label();
                    private final Label typeLabel = new Label();
                    private final Label dateFactLabel = new Label();
                    private final Label dateLimiteLabel = new Label();
                    private final Label stateLabel = new Label();
                    private final Label prixLabel = new Label();
                    private final HBox buttonContainer = new HBox(5);
                    private final Button editBtn = new Button();
                    private final Button deleteBtn = new Button();
                    private final GridPane grid = new GridPane();
                    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    {
                        grid.getColumnConstraints().addAll(createColumnConstraints());
                        grid.setHgap(15);
                        grid.setVgap(5);
                        grid.setPadding(new Insets(10));
                        grid.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

                        grid.add(cinLabel, 0, 0);
                        grid.add(typeLabel, 1, 0);
                        grid.add(dateFactLabel, 2, 0);
                        grid.add(dateLimiteLabel, 3, 0);
                        grid.add(stateLabel, 4, 0);
                        grid.add(prixLabel, 5, 0);
                        grid.add(buttonContainer, 6, 0);

                        FontIcon editIcon = new FontIcon(FontAwesome.EDIT);
                        FontIcon deleteIcon = new FontIcon(FontAwesome.TRASH);
                        editIcon.setIconSize(16);
                        deleteIcon.setIconSize(16);
                        deleteIcon.setIconColor(Color.RED);

                        editBtn.setGraphic(editIcon);
                        deleteBtn.setGraphic(deleteIcon);
                        editBtn.setStyle("-fx-background-color: transparent;");
                        deleteBtn.setStyle("-fx-background-color: transparent;");

                        buttonContainer.getChildren().addAll(editBtn, deleteBtn);

                        editBtn.setOnAction(event -> handleUpdate(getItem()));
                        deleteBtn.setOnAction(event -> handleDelete(getItem()));
                    }

                    @Override
                    protected void updateItem(Facture facture, boolean empty) {
                        super.updateItem(facture, empty);
                        if (empty || facture == null) {
                            setGraphic(null);
                        } else {
                            cinLabel.setText(facture.getUserCIN());
                            typeLabel.setText(facture.getTypeFacture());
                            dateFactLabel.setText(facture.getDateFacture().format(formatter));
                            dateLimiteLabel.setText(facture.getDateLimitePaiement().format(formatter));
                            prixLabel.setText(String.format("%.3f TND", facture.getPrixFact()));
                            stateLabel.setText(facture.isState() ? "Payée" : "Impayée");
                            stateLabel.setTextFill(facture.isState() ? Color.GREEN : Color.RED);
                            setGraphic(grid);
                        }
                    }
                };
            }
        });
    }

    private void handleDelete(Facture facture) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette facture ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    factureService.delete(facture.getId());
                    refreshList();
                } catch (SQLException e) {
                    showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
                }
            }
        });
    }

    public void refreshList() {
        try {
            factureData.setAll(factureService.readList());
            factureList.setItems(factureData);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données : " + e.getMessage());
        }
    }



    // ------------------- Dialogs with Input Validation --------------------

    // Validate that all required fields are provided and valid.
    private void validateAllFields(String cin, String type, LocalDate dateFact, LocalDate dateLimite, String prixText) throws Exception {
        if (cin == null || cin.trim().isEmpty() ||
                type == null || type.trim().isEmpty() ||
                dateFact == null || dateLimite == null ||
                prixText == null || prixText.trim().isEmpty()) {
            showAlert("Validation Error", "Tous les champs obligatoires doivent être remplis");
            throw new Exception("Tous les champs obligatoires doivent être remplis");
        }
        if (!cin.matches("\\d{8}")) {
            showAlert("Validation Error", "Le CIN doit contenir exactement 8 chiffres");
            throw new Exception("Le CIN doit contenir exactement 8 chiffres");
        }
        if (!prixText.matches("\\d+(\\.\\d{1,3})?")) {
            showAlert("Validation Error", "Format montant invalide (ex: 123.456)");
            throw new Exception("Format montant invalide (ex: 123.456)");
        }
        if (dateLimite.isBefore(dateFact)) {
            showAlert("Validation Error", "La date d'échéance doit être postérieure à la date de facturation");
            throw new Exception("La date d'échéance doit être postérieure à la date de facturation");
        }
        float prix = Float.parseFloat(prixText);
        if (prix <= 0) {
            showAlert("Validation Error", "Le montant doit être positif");
            throw new Exception("Le montant doit être positif");
        }
    }

    private GridPane createFormPane() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #ffffff;");
        return grid;
    }

    private HBox createInputGroup(String labelText, FontAwesome icon, Control input) {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        FontIcon inputIcon = new FontIcon(icon);
        inputIcon.setIconSize(18);
        inputIcon.setIconColor(Color.GRAY);

        VBox wrapper = new VBox(5);
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");

        HBox inputBox = new HBox(10);
        inputBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 8;");
        inputBox.getChildren().addAll(inputIcon, input);

        wrapper.getChildren().addAll(label, inputBox);
        container.getChildren().add(wrapper);
        return container;
    }

    private void styleDialog(Dialog<?> dialog, String title, FontAwesome icon) {
        URL cssResource = getClass().getResource("/tn/esprit/gui/dialogs.css");
        if (cssResource != null) {
            dialog.getDialogPane().getStylesheets().add(cssResource.toExternalForm());
        } else {
            System.err.println("CSS resource not found!");
        }
        dialog.setHeaderText(null);
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #4a90e2; -fx-padding: 20;");

        FontIcon headerIcon = new FontIcon(icon);
        headerIcon.setIconSize(30);
        headerIcon.setIconColor(Color.WHITE);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        header.getChildren().addAll(headerIcon, titleLabel);

        dialog.getDialogPane().setHeader(header);
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/tn/esprit/gui/dialogs.css").toExternalForm()
        );
    }

    private void styleDialogButtons(Dialog<?> dialog) {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold;");
        okButton.setGraphic(new FontIcon(FontAwesome.CHECK));

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        cancelButton.setGraphic(new FontIcon(FontAwesome.TIMES));
    }

    // ----------------- Handle Update Dialog -----------------
    private void handleUpdate(Facture facture) {
        Dialog<Facture> dialog = new Dialog<>();
        styleDialog(dialog, "Modifier Facture", FontAwesome.EDIT);

        GridPane grid = createFormPane();
        Label validationLabel = new Label();
        validationLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12;");

        TextField cinField = new TextField(facture.getUserCIN());
        TextField typeField = new TextField(facture.getTypeFacture());
        DatePicker dateFacturePicker = new DatePicker(facture.getDateFacture());
        DatePicker dateLimitePicker = new DatePicker(facture.getDateLimitePaiement());
        TextField prixField = new TextField(String.valueOf(facture.getPrixFact()));
        CheckBox stateCheck = new CheckBox("Payée");
        stateCheck.setSelected(facture.isState());

        grid.addRow(0,
                createInputGroup("CIN Client", FontAwesome.USER, cinField),
                createInputGroup("Type Facture", FontAwesome.FILE_TEXT, typeField)
        );
        grid.addRow(1,
                createInputGroup("Date Facturation", FontAwesome.CALENDAR, dateFacturePicker),
                createInputGroup("Date Échéance", FontAwesome.CALENDAR_CHECK_O, dateLimitePicker)
        );
        grid.addRow(2,
                createInputGroup("Montant (TND)", FontAwesome.MONEY, prixField),
                createInputGroup("État", FontAwesome.CHECK_SQUARE_O, stateCheck)
        );
        grid.add(validationLabel, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogButtons(dialog);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    // Validate all input fields.
                    validateAllFields(
                            cinField.getText(),
                            typeField.getText(),
                            dateFacturePicker.getValue(),
                            dateLimitePicker.getValue(),
                            prixField.getText()
                    );
                    return updateFacture(facture, dateFacturePicker, dateLimitePicker,
                            prixField, typeField, cinField, stateCheck);
                } catch (Exception e) {
                    validationLabel.setText(e.getMessage());

                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::updateAndRefresh);
    }

    // ----------------- Handle Add Dialog -----------------
    @FXML
    private void handleAddDialog() {
        Dialog<Facture> dialog = new Dialog<>();
        styleDialog(dialog, "Nouvelle Facture", FontAwesome.PLUS);

        GridPane grid = createFormPane();
        Label validationLabel = new Label();
        validationLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12;");

        TextField cinField = new TextField();
        TextField typeField = new TextField();
        DatePicker dateFacturePicker = new DatePicker();
        DatePicker dateLimitePicker = new DatePicker();
        TextField prixField = new TextField();
        CheckBox stateCheck = new CheckBox("Payée");

        grid.addRow(0,
                createInputGroup("CIN Client", FontAwesome.USER, cinField),
                createInputGroup("Type Facture", FontAwesome.FILE_TEXT, typeField)
        );
        grid.addRow(1,
                createInputGroup("Date Facturation", FontAwesome.CALENDAR, dateFacturePicker),
                createInputGroup("Date Échéance", FontAwesome.CALENDAR_CHECK_O, dateLimitePicker)
        );
        grid.addRow(2,
                createInputGroup("Montant (TND)", FontAwesome.MONEY, prixField),
                createInputGroup("État", FontAwesome.CHECK_SQUARE_O, stateCheck)
        );
        grid.add(validationLabel, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogButtons(dialog);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    validateAllFields(
                            cinField.getText(),
                            typeField.getText(),
                            dateFacturePicker.getValue(),
                            dateLimitePicker.getValue(),
                            prixField.getText()
                    );
                    return createNewFacture(dateFacturePicker, dateLimitePicker,
                            prixField, typeField, cinField, stateCheck);
                } catch (Exception e) {
                    validationLabel.setText(e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::addAndRefresh);
    }

    private Facture updateFacture(Facture facture, DatePicker dateFact, DatePicker dateLimite,
                                  TextField prix, TextField type, TextField cin, CheckBox state) {
        facture.setDateFacture(dateFact.getValue());
        facture.setDateLimitePaiement(dateLimite.getValue());
        facture.setPrixFact(Float.parseFloat(prix.getText()));
        facture.setTypeFacture(type.getText());
        facture.setUserCIN(cin.getText());
        facture.setState(state.isSelected());
        return facture;
    }

    private Facture createNewFacture(DatePicker dateFact, DatePicker dateLimite,
                                     TextField prix, TextField type, TextField cin, CheckBox state) {
        return new Facture(0, dateFact.getValue(), dateLimite.getValue(),
                Float.parseFloat(prix.getText()), type.getText(),
                state.isSelected(), cin.getText());
    }

    private void updateAndRefresh(Facture facture) {
        try {
            factureService.update(facture);
            refreshList();
        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
        }
    }

    private void addAndRefresh(Facture facture) {
        try {
            factureService.add(facture);
            refreshList();
        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
        }
    }

    private void insertAddButtonAboveList() {
        Button addButton = new Button("Ajouter Facture");
        FontIcon plusIcon = new FontIcon(FontAwesome.PLUS);
        plusIcon.setIconSize(20);
        addButton.setGraphic(plusIcon);
        addButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-padding: 8 15;");
        addButton.setOnAction(e -> handleAddDialog());

        Parent parent = factureList.getParent();
        if (parent instanceof Pane) {
            Pane container = (Pane) parent;
            int listIndex = container.getChildren().indexOf(factureList);
            if(listIndex >= 0) {
                container.getChildren().add(listIndex, addButton);
            } else {
                container.getChildren().add(0, addButton);
            }
            if (container instanceof AnchorPane) {
                AnchorPane anchorPane = (AnchorPane) container;
                AnchorPane.setTopAnchor(addButton, 30.0);
                AnchorPane.setLeftAnchor(addButton, 10.0);
                AnchorPane.setTopAnchor(factureList, 60.0);
            }
        }
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
        Stage stage = (Stage) factureList.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Gestion du Blog");
        stage.show();
    }

}
