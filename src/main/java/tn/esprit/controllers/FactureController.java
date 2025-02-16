package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import tn.esprit.entities.Facture;
import tn.esprit.services.FactureServices;

import java.sql.SQLException;
import java.time.LocalDate;

public class FactureController {

    @FXML private TableView<Facture> factureTable;
    private final FactureServices factureService = new FactureServices();
    private final ObservableList<Facture> factureData = FXCollections.observableArrayList();

    @FXML
    public void initializeAdminData() {
        configureTableColumns();
        refreshTable();
        addActionColumn();
    }
    private void configureTableColumns() {
        factureTable.getColumns().clear();

        TableColumn<Facture, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Facture, LocalDate> dateFactCol = new TableColumn<>("Date Facture");
        dateFactCol.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));

        TableColumn<Facture, LocalDate> dateLimiteCol = new TableColumn<>("Date Limite");
        dateLimiteCol.setCellValueFactory(new PropertyValueFactory<>("dateLimitePaiement"));

        TableColumn<Facture, Float> prixCol = new TableColumn<>("Prix");
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixFact"));
        prixCol.setPrefWidth(80);

        TableColumn<Facture, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeFacture"));
        typeCol.setPrefWidth(150);

        TableColumn<Facture, Boolean> stateCol = new TableColumn<>("État");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateCol.setPrefWidth(80);

        TableColumn<Facture, String> cinCol = new TableColumn<>("CIN");
        cinCol.setCellValueFactory(new PropertyValueFactory<>("userCIN"));
        cinCol.setPrefWidth(100);

        factureTable.getColumns().addAll(idCol, dateFactCol, dateLimiteCol, prixCol, typeCol, stateCol, cinCol);
    }

    private void addActionColumn() {
        TableColumn<Facture, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);

        actionCol.setCellFactory(param -> new TableCell<Facture, Void>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("edit-btn");
                deleteBtn.getStyleClass().add("delete-btn");

                editBtn.setOnAction(event -> {
                    Facture facture = getTableView().getItems().get(getIndex());
                    handleUpdate(facture);
                });

                deleteBtn.setOnAction(event -> {
                    Facture facture = getTableView().getItems().get(getIndex());
                    handleDelete(facture);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        factureTable.getColumns().add(actionCol);
    }

    private void handleUpdate(Facture facture) {
        Dialog<Facture> dialog = new Dialog<>();
        dialog.setTitle("Modifier Facture");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        DatePicker dateFacturePicker = new DatePicker(facture.getDateFacture());
        DatePicker dateLimitePicker = new DatePicker(facture.getDateLimitePaiement());
        TextField prixField = new TextField(String.valueOf(facture.getPrixFact()));
        TextField typeField = new TextField(facture.getTypeFacture());
        TextField cinField = new TextField(facture.getUserCIN());
        CheckBox stateCheck = new CheckBox("Payée");
        stateCheck.setSelected(facture.isState());

        grid.addRow(0, new Label("Date Facture:"), dateFacturePicker);
        grid.addRow(1, new Label("Date Limite:"), dateLimitePicker);
        grid.addRow(2, new Label("Prix:"), prixField);
        grid.addRow(3, new Label("Type:"), typeField);
        grid.addRow(4, new Label("CIN:"), cinField);
        grid.addRow(5, new Label("État:"), stateCheck);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    if (!cinField.getText().matches("\\d{8}")) {
                        throw new IllegalArgumentException("CIN doit contenir 8 chiffres");
                    }

                    facture.setDateFacture(dateFacturePicker.getValue());
                    facture.setDateLimitePaiement(dateLimitePicker.getValue());
                    facture.setPrixFact(Float.parseFloat(prixField.getText()));
                    facture.setTypeFacture(typeField.getText());
                    facture.setState(stateCheck.isSelected());
                    facture.setUserCIN(cinField.getText());

                    return facture;
                } catch (Exception e) {
                    showAlert("Erreur", e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedFacture -> {
            try {
                factureService.update(updatedFacture);
                refreshTable();
            } catch (SQLException e) {
                showAlert("Erreur SQL", e.getMessage());
            }
        });
    }

    @FXML
    private void handleAddDialog() {
        Dialog<Facture> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Facture");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        DatePicker dateFacturePicker = new DatePicker();
        DatePicker dateLimitePicker = new DatePicker();
        TextField prixField = new TextField();
        TextField typeField = new TextField();
        TextField cinField = new TextField();
        CheckBox stateCheck = new CheckBox("Payée");

        grid.addRow(0, new Label("Date Facture:"), dateFacturePicker);
        grid.addRow(1, new Label("Date Limite:"), dateLimitePicker);
        grid.addRow(2, new Label("Prix:"), prixField);
        grid.addRow(3, new Label("Type:"), typeField);
        grid.addRow(4, new Label("CIN (8 chiffres):"), cinField);
        grid.addRow(5, new Label("État:"), stateCheck);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    if (!cinField.getText().matches("\\d{8}")) {
                        throw new IllegalArgumentException("CIN invalide : doit contenir 8 chiffres");
                    }

                    return new Facture(
                            0,
                            dateFacturePicker.getValue(),
                            dateLimitePicker.getValue(),
                            Float.parseFloat(prixField.getText()),
                            typeField.getText(),
                            stateCheck.isSelected(),
                            cinField.getText()
                    );
                } catch (Exception e) {
                    showAlert("Erreur", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(facture -> {
            try {
                factureService.add(facture);
                refreshTable();
            } catch (SQLException e) {
                showAlert("Erreur SQL", e.getMessage());
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
                    refreshTable();
                } catch (SQLException e) {
                    showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
                }
            }
        });
    }

    public void refreshTable() {
        try {
            factureData.setAll(factureService.readList());
            factureTable.setItems(factureData);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}