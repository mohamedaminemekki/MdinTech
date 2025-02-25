package tn.esprit.market_3a33.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.market_3a33.entities.Product;
import tn.esprit.market_3a33.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private FlowPane productContainer;
    @FXML
    private ListView<String> cartListView;  // Main page cart view (displays text details)
    @FXML
    private Label totalPriceLabel;

    private List<Product> products = new ArrayList<>();
    private double totalPrice = 0.0;
    private List<Product> cartProducts = new ArrayList<>(); // Holds selected products

    @FXML
    public void initialize() {
        loadProductsFromDatabase();
        displayProducts();
    }

    private void loadProductsFromDatabase() {
        String query = "SELECT p.id, p.name, p.reference, p.price, p.stockLimit, COALESCE(s.quantity, 0) AS stock " +
                "FROM products p " +
                "LEFT JOIN stock s ON p.id = s.productId";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String reference = rs.getString("reference");
                double price = rs.getDouble("price");
                int stockLimit = rs.getInt("stockLimit");
                int stock = rs.getInt("stock");
                String imagePath = "/tn/esprit/market_3a33/images/product" + id + ".jpg";
                products.add(new Product(id, name, reference, price, stockLimit, stock, imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayProducts() {
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productContainer.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");

        ImageView imageView = new ImageView(new Image(getClass().getResource(product.getImagePath()).toExternalForm()));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("%.2f dt", product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        // Replace Spinner with TextField for quantity input
        TextField quantityField = new TextField("1");
        quantityField.getStyleClass().add("quantity-field");

        Button addButton = new Button("Add to Cart");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> addToCart(product, quantityField));

        card.getChildren().addAll(imageView, nameLabel, priceLabel, quantityField, addButton);
        return card;
    }

    private void addToCart(Product product, TextField quantityField) {
        try {
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity < 1 || quantity > product.getStock()) {
                showAlert("Invalid Quantity", "Please enter a valid quantity between 1 and " + product.getStock() + ".");
                return;
            }

            // Add the selected product the given number of times to the cartProducts list.
            for (int i = 0; i < quantity; i++) {
                cartProducts.add(product);
            }
            double totalProductPrice = product.getPrice() * quantity;
            totalPrice += totalProductPrice;
            totalPriceLabel.setText(String.format("Total: %.2f dt", totalPrice));

            // Update the main page ListView to display a textual summary.
            cartListView.getItems().add(product.getName() + " x" + quantity + " - " + String.format("%.2f dt", totalProductPrice));

            // Reset the quantity field
            quantityField.setText("1");

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for quantity.");
        }
    }

    @FXML
    private void confirmOrder() {
        try {
            // Load the cart FXML page.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/market_3a33/cart.fxml"));
            Parent root = loader.load();

            // Pass the cartProducts list and totalPrice to the CartController.
            CartController cartController = loader.getController();
            cartController.setCartData(cartProducts, totalPrice);

            // Create and show a new window for the cart page.
            Stage stage = new Stage();
            stage.setTitle("Shopping Cart");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}