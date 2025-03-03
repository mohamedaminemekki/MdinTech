package tn.esprit.market_3a33.Controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import tn.esprit.market_3a33.entities.Product;
import tn.esprit.market_3a33.services.ProductService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StockManagementController {

    @FXML
    private TableView<Product> lowStockTable;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Integer> currentStockColumn;

    @FXML
    private TextField stockInput;

    @FXML
    private Label invoiceLabel;

    private ObservableList<Product> lowStockProducts = FXCollections.observableArrayList();
    private ObservableList<Product> productsWithAddedStock = FXCollections.observableArrayList(); // Track products with added stock
    private ProductService productDAO = new ProductService();

    @FXML
    public void initialize() {
        // Set up table columns
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        currentStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Load low stock products
        loadLowStockProducts();
    }

    private void loadLowStockProducts() {
        lowStockProducts.clear();
        lowStockProducts.addAll(productDAO.getLowStockProducts());
        lowStockTable.setItems(lowStockProducts);
    }

    @FXML
    private void handleAddStock() {
        Product selectedProduct = lowStockTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("No Product Selected", "Please select a product from the table.");
            return;
        }

        try {
            int stockToAdd = Integer.parseInt(stockInput.getText());
            if (stockToAdd <= 0) {
                showAlert("Invalid Input", "Please enter a positive number for stock.");
                return;
            }

            // Update stock in the database
            productDAO.updateStock(selectedProduct.getId(), stockToAdd, "Warehouse"); // Assuming location is "Warehouse"

            // Update the product's stock in the table
            selectedProduct.setStock(selectedProduct.getStock() + stockToAdd);
            lowStockTable.refresh();
            stockInput.clear();

            // Add the product to the list of products with added stock
            if (!productsWithAddedStock.contains(selectedProduct)) {
                productsWithAddedStock.add(selectedProduct);
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for stock.");
        }
    }

    @FXML
    private void handleGenerateInvoice() {
        if (productsWithAddedStock.isEmpty()) {
            showAlert("No Products", "No products with added stock available to generate an invoice.");
            return;
        }

        double total = 0.0;
        for (Product product : productsWithAddedStock) {
            total += product.getPrice() * product.getStock();
        }
        double totalPrice = total;
        double facture = totalPrice * 81 / 100; // Assuming 81% is the facture calculation
        invoiceLabel.setText(String.format("Facture: %.2f dt", facture));

        // Generate PDF invoice
        generatePdfInvoice(totalPrice, facture);

        // Clear the products with added stock after generating the invoice
        productsWithAddedStock.clear();
    }

    private void generatePdfInvoice(double totalPrice, double facture) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Invoice");
        fileChooser.setInitialFileName("invoice.pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                // Add title
                document.add(new Paragraph("Invoice")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(20)
                        .setBold());

                // Add company information
                document.add(new Paragraph("Company Name: mdinTech")
                        .setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph("Address: 123 Main St, Tunis, tunisie")
                        .setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph("Phone: +123 456 7890")
                        .setTextAlignment(TextAlignment.LEFT));

                // Add a table for products
                Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2}));
                table.setWidth(UnitValue.createPercentValue(100));

                // Add table headers
                table.addHeaderCell("Product Name");
                table.addHeaderCell("Quantity");
                table.addHeaderCell("Price");
                table.addHeaderCell("Total");

                // Add products to the table
                for (Product product : productsWithAddedStock) {
                    table.addCell(product.getName());
                    table.addCell(String.valueOf(product.getStock()));
                    table.addCell(String.format("%.2f dt", product.getPrice()));
                    table.addCell(String.format("%.2f dt", product.getPrice() * product.getStock()));
                }

                document.add(table);

                // Add total price and facture
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(String.format("Total Price: %.2f dt", totalPrice))
                        .setTextAlignment(TextAlignment.RIGHT));
                document.add(new Paragraph(String.format("Facture: %.2f dt", facture))
                        .setTextAlignment(TextAlignment.RIGHT));

            } catch (FileNotFoundException e) {
                showAlert("Error", "Failed to save the invoice file.");
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadLowStockProducts(); // Reload low stock products from the database
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}