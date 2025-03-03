package tn.esprit.market_3a33.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.market_3a33.entities.Product;
import tn.esprit.market_3a33.services.ProductService;

import java.io.IOException;

public class ProductManagementController {

    @FXML
    private ListView<Product> productListView;
    private ProductService productDAO = new ProductService();

    @FXML
    public void initialize() {
        // Load products into the ListView
        refreshListView();
    }

    @FXML
    private void handleAddProduct() {
        showProductDialog(null);
    }

    @FXML
    private void handleEditProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            showProductDialog(selectedProduct);
        } else {
            showAlert("No Selection", "Please select a product to edit.");
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            productDAO.deleteProduct(selectedProduct.getId());
            refreshListView();
        } else {
            showAlert("No Selection", "Please select a product to delete.");
        }
    }

    private void showProductDialog(Product product) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/market_3a33/product_dialog.fxml"));
            VBox dialogPane = loader.load();

            // Get the controller
            ProductDialogController controller = loader.getController();
            controller.setProduct(product);

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle(product == null ? "Add Product" : "Edit Product");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(dialogPane));

            // Set the dialog stage in the controller
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait for the user to close it
            dialogStage.showAndWait();

            // If the user clicked OK, save the product
            if (controller.isOkClicked()) {
                Product updatedProduct = controller.getProduct();
                if (product == null) {
                    productDAO.addProduct(updatedProduct);
                } else {
                    productDAO.updateProduct(updatedProduct);
                }
                refreshListView();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the product dialog.");
        }
    }

    private void refreshListView() {
        productListView.getItems().setAll(productDAO.getAllProducts());
        productListView.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Customize the display of each product in the ListView
                    setText(product.getName() + " - " + product.getReference() + "\n" +
                            "Price: " + product.getPrice() + " dt\n" +
                            "Stock: " + product.getStock() + " / " + product.getStockLimit());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}