package tn.esprit.market_3a33.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.market_3a33.entities.Product;

public class ProductDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField referenceField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockLimitField;
    @FXML
    private TextField stockField;

    private Stage dialogStage;
    private Product product;
    private boolean okClicked = false;

    @FXML
    public void initialize() {
        // No need for KeyEvent filters anymore
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProduct(Product product) {
        this.product = product;

        if (product != null) {
            nameField.setText(product.getName());
            referenceField.setText(product.getReference());
            priceField.setText(String.valueOf(product.getPrice()));
            stockLimitField.setText(String.valueOf(product.getStockLimit()));
            stockField.setText(String.valueOf(product.getStock()));
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public Product getProduct() {
        if (product == null) {
            product = new Product();
        }
        product.setName(nameField.getText());
        product.setReference(referenceField.getText());
        product.setPrice(parseDouble(priceField.getText()));
        product.setStockLimit(parseInt(stockLimitField.getText()));
        product.setStock(parseInt(stockField.getText()));
        return product;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        // Validate nameField (only letters and spaces)
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Name is required!\n";
        } else if (!nameField.getText().matches("[a-zA-Z ]+")) {
            errorMessage += "Name can only contain letters and spaces!\n";
        }

        // Validate referenceField (no specific format, just not empty)
        if (referenceField.getText() == null || referenceField.getText().isEmpty()) {
            errorMessage += "Reference is required!\n";
        }

        // Validate priceField (only real numbers)
        if (priceField.getText() == null || priceField.getText().isEmpty()) {
            errorMessage += "Price is required!\n";
        } else if (!priceField.getText().matches("\\d*(\\.\\d*)?")) {
            errorMessage += "Price must be a valid number!\n";
        }

        // Validate stockLimitField (only integers)
        if (stockLimitField.getText() == null || stockLimitField.getText().isEmpty()) {
            errorMessage += "Stock Limit is required!\n";
        } else if (!stockLimitField.getText().matches("\\d*")) {
            errorMessage += "Stock Limit must be a valid integer!\n";
        }

        // Validate stockField (only integers)
        if (stockField.getText() == null || stockField.getText().isEmpty()) {
            errorMessage += "Stock is required!\n";
        } else if (!stockField.getText().matches("\\d*")) {
            errorMessage += "Stock must be a valid integer!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Invalid Fields", errorMessage);
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}