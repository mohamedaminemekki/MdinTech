package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entities.Facture;
import tn.esprit.entities.User;
import tn.esprit.services.FactureServices;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserController {

    @FXML private TableView<Facture> factureTable;
    @FXML private TableColumn<Facture, Integer> colId;
    @FXML private TableColumn<Facture, LocalDate> colDateFacture;
    @FXML private TableColumn<Facture, LocalDate> colDateLimite;
    @FXML private TableColumn<Facture, Float> colMontant;
    @FXML private TableColumn<Facture, String> colType;
    @FXML private TableColumn<Facture, LocalDate> colDatePaiement;
    @FXML private TableColumn<Facture, Void> colAction;

    private final FactureServices factureService = new FactureServices();
    private final ObservableList<Facture> factureData = FXCollections.observableArrayList();
    private User currentUser;

    @FXML
    public void initialize() {
        configureTableColumns();
    }

    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDateFacture.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));
        colDateLimite.setCellValueFactory(new PropertyValueFactory<>("dateLimitePaiement"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("prixFact"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeFacture"));
        colDatePaiement.setCellValueFactory(new PropertyValueFactory<>("datePaiement"));

        colAction.setCellFactory(column -> new TableCell<>() {
            private final Button btnPayer = new Button("Payer");

            {
                btnPayer.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnPayer.setOnAction(event -> {
                    Facture facture = getTableView().getItems().get(getIndex());
                    openPaymentWindow(facture);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().isEmpty()) {
                    setGraphic(null);
                } else {
                    Facture facture = getTableView().getItems().get(getIndex());
                    setGraphic(facture.isState() ? null : btnPayer);
                }
            }
        });
    }

    private void openPaymentWindow(Facture facture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/gui/Payment.fxml"));
            Parent root = loader.load();

            PaymentController controller = loader.getController();
            controller.initializeData(facture, this::loadUserFactures);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Paiement SONEDE");
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'interface de paiement");
        }

    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadUserFactures();
    }

    private void loadUserFactures() {
        try {
            factureData.setAll(factureService.getFacturesByUser(currentUser.getCin()));
            factureTable.setItems(factureData);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des données");
        }
    }

    public void refreshTable() {
        loadUserFactures();
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}