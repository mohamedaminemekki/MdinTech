package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Facture;
import tn.esprit.entities.User;
import tn.esprit.services.FactureServices;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserController {

    @FXML private ListView<Facture> unpaidList;
    @FXML private ListView<Facture> paidList;
    @FXML private Label pageTitle;

    private final FactureServices factureService = new FactureServices();
    private final ObservableList<Facture> factureData = FXCollections.observableArrayList();
    private User currentUser;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Configure both ListViews with the same cell factory.
        configureListCells(unpaidList);
        configureListCells(paidList);
        addColumnHeaders(); // Add this line
        showUnpaid();
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
                "Date Fact", "Échéance", "Montant (TND)",
                "Type", "Statut", "Actions"
        };

        for (int i = 0; i < headers.length; i++) {
            Label headerLabel = new Label(headers[i]);
            headerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            header.add(headerLabel, i, 0);
        }

        // Add headers to the parent container of BOTH ListViews
        Parent parent = unpaidList.getParent();
        if (parent instanceof Pane) {
            Pane container = (Pane) parent;
            if (!container.getChildren().contains(header)) {
                // Add headers just after the title label (assuming it's the first child)
                container.getChildren().add(1, header);
            }
        }
    }

    private void configureListCells(ListView<Facture> listView) {
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Facture> call(ListView<Facture> param) {
                return new ListCell<Facture>() {
                    private final GridPane grid = new GridPane();
                    private final Label dateFactLabel = new Label();
                    private final Label dateLimiteLabel = new Label();
                    private final Label prixLabel = new Label();
                    private final Label typeLabel = new Label();
                    private final Label statusLabel = new Label();
                    private final Button payButton = new Button("Payer");

                    {
                        grid.getColumnConstraints().addAll(createColumnConstraints());
                        grid.setHgap(15);
                        grid.setVgap(5);
                        grid.setPadding(new Insets(10));
                        grid.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

                        grid.addColumn(0, dateFactLabel);
                        grid.addColumn(1, dateLimiteLabel);
                        grid.addColumn(2, prixLabel);
                        grid.addColumn(3, typeLabel);
                        grid.addColumn(4, statusLabel);
                        grid.add(payButton, 5, 0);

                        payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        payButton.setOnAction(event -> {
                            Facture facture = getItem();
                            if (facture != null && !facture.isState()) {
                                openPaymentWindow(facture);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Facture facture, boolean empty) {
                        super.updateItem(facture, empty);
                        if (empty || facture == null) {
                            setGraphic(null);
                        } else {
                            dateFactLabel.setText(formatDate(facture.getDateFacture()));
                            dateLimiteLabel.setText(formatDate(facture.getDateLimitePaiement()));
                            prixLabel.setText(String.format("%.3f TND", facture.getPrixFact()));
                            typeLabel.setText(facture.getTypeFacture());

                            if (facture.isState()) {
                                String paidText = facture.getDatePaiement() != null
                                        ? formatDate(facture.getDatePaiement()) : "N/A";
                                statusLabel.setText("Payé le " + paidText);
                                statusLabel.setTextFill(Color.GREEN);
                            } else {
                                statusLabel.setText("Impayé");
                                statusLabel.setTextFill(Color.RED);
                            }

                            payButton.setVisible(!facture.isState());
                            setGraphic(grid);
                        }
                    }

                    private String formatDate(LocalDate date) {
                        return date != null ? date.format(formatter) : "N/A";
                    }
                };
            }
        });
    }

    private ColumnConstraints[] createColumnConstraints() {
        double[] widths = {120, 120, 100, 150, 100, 80};
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
            ObservableList<Facture> unpaidData = FXCollections.observableArrayList();
            ObservableList<Facture> paidData = FXCollections.observableArrayList();
            for (Facture f : factureData) {
                if (!f.isState()) {
                    unpaidData.add(f);
                } else {
                    paidData.add(f);
                }
            }
            unpaidList.setItems(unpaidData);
            paidList.setItems(paidData);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement des données: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Methods called by the drawer buttons:
    @FXML
    private void showUnpaid() {
        pageTitle.setText("Mes Factures à Payer");
        unpaidList.setVisible(true);
        unpaidList.setManaged(true);
        paidList.setVisible(false);
        paidList.setManaged(false);
    }

    @FXML
    private void showPaid() {
        pageTitle.setText("Historique des Paiements");
        unpaidList.setVisible(false);
        unpaidList.setManaged(false);
        paidList.setVisible(true);
        paidList.setManaged(true);
    }
}
