package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Facture;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReceiptController implements Initializable {

    // Déclarations FXML
    @FXML private AnchorPane rootPane;
    @FXML private Button btnPrint;
    @FXML private Label lblPaymentDate;
    @FXML private Label lblInvoiceNumber;
    @FXML private Label lblAmount;
    @FXML private Label lblPaymentMethod;
    @FXML private Label lblClient;
    @FXML private Label lblTransactionId;

    private Facture facture;
    private String paymentMethod;
    private String lastDigits;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateLabels();
    }

    private void updateLabels() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblPaymentDate.setText(LocalDateTime.now().format(formatter));

        if(facture != null) {
            lblInvoiceNumber.setText("FACT-" + facture.getId());
            lblAmount.setText(String.format("%.3f TND", facture.getPrixFact()));
            lblClient.setText(facture.getUserCIN());
            lblTransactionId.setText(generateTransactionId());
        }

        if(paymentMethod != null) {
            lblPaymentMethod.setText(paymentMethod + " ****" + lastDigits);
        }
    }

    public void setPaymentDetails(Facture facture, String paymentMethod, String lastDigits) {
        this.facture = facture;
        this.paymentMethod = paymentMethod;
        this.lastDigits = lastDigits;
        updateLabels();
    }
    @FXML
    private void handlePrint() {
        if (rootPane == null) {
            showAlert("Erreur", "Élément d'impression non initialisé", Alert.AlertType.ERROR);
            return;
        }

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            boolean proceed = printerJob.showPrintDialog(btnPrint.getScene().getWindow());
            if (proceed) {
                boolean success = printerJob.printPage(rootPane);
                if (success) {
                    printerJob.endJob();
                    showAlert("Impression", "Reçu imprimé avec succès", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec de l'impression", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Erreur", "Aucune imprimante détectée", Alert.AlertType.ERROR);
        }
    }

    private String generateTransactionId() {
        return "TRX-" + System.currentTimeMillis();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}