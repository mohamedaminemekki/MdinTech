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
import tn.esprit.market_3a33.entities.Produit;
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

    private List<Produit> products = new ArrayList<>();
    private double totalPrice = 0.0;
    private List<Produit> cartProducts = new ArrayList<>(); // Holds selected products

    @FXML
    public void initialize() {
        loadProductsFromDatabase();
        displayProducts();
    }

    private void loadProductsFromDatabase() {
        String query = "SELECT * FROM products";
        try (Connection conn = MyDatabase.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String imagePath = "/tn/esprit/market_3a33/images/product" + id + ".jpg";
                products.add(new Produit(id, name, price, imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayProducts() {
        for (Produit product : products) {
            VBox productCard = createProductCard(product);
            productContainer.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Produit product) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");

        ImageView imageView = new ImageView(new Image(getClass().getResource(product.getImagePath()).toExternalForm()));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("%.2f dt", product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        quantitySpinner.getStyleClass().add("quantity-spinner");

        Button addButton = new Button("Add to Cart");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> addToCart(product, quantitySpinner));

        card.getChildren().addAll(imageView, nameLabel, priceLabel, quantitySpinner, addButton);
        return card;
    }

    private void addToCart(Produit product, Spinner<Integer> quantitySpinner) {
        int quantity = quantitySpinner.getValue();
        // Add the selected product the given number of times to the cartProducts list.
        for (int i = 0; i < quantity; i++) {
            cartProducts.add(product);
        }
        double totalProductPrice = product.getPrice() * quantity;
        totalPrice += totalProductPrice;
        totalPriceLabel.setText(String.format("Total: %.2f dt", totalPrice));

        // Update the main page ListView to display a textual summary.
        cartListView.getItems().add(product.getName() + " x" + quantity + " - " + String.format("%.2f dt", totalProductPrice));

        // Reset the quantity spinner.
        quantitySpinner.getValueFactory().setValue(1);
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

            // Optionally, if you want to update the main page cart display (e.g., add a note that the order has been sent)
            // you may choose to leave cartProducts intact.
            // In this example, both the main page and the cart page display the selected products.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
