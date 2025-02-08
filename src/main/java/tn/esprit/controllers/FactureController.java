package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Facture;
import tn.esprit.services.FactureServices;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

public class FactureController {
    @FXML private DatePicker dateField;
    @FXML private TextField prixField;
    @FXML private TextField typeField;
    @FXML private CheckBox stateCheckbox;
    @FXML private TableView<Facture> factureTable;

    private final FactureServices factureService = new FactureServices();
    private final ObservableList<Facture> factureData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        refreshTable();
    }

    @FXML
    private void handleAdd() {
        try {
            Facture newFacture = new Facture(
                    0,
                    Date.from(dateField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Float.parseFloat(prixField.getText()),
                    typeField.getText(),
                    stateCheckbox.isSelected(),
                    1 // Default user ID
            );

            factureService.add(newFacture);
            refreshTable();
            clearFields();
        } catch (SQLException | NumberFormatException e) {
            showAlert("Erreur", "Données invalides: " + e.getMessage());
        }
    }
    @FXML
    private void handleDelete() {
        Facture selectedFacture = factureTable.getSelectionModel().getSelectedItem();
        if (selectedFacture != null) {
            try {
                factureService.delete(selectedFacture.getId());
                refreshTable();
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer la facture: " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une facture à supprimer.");
        }
    }


    private void refreshTable() {
        try {
            factureData.setAll(factureService.readList());
            factureTable.setItems(factureData);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void clearFields() {
        dateField.setValue(null);
        prixField.clear();
        typeField.clear();
        stateCheckbox.setSelected(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}