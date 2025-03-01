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
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.market_3a33.entities.Product;
import tn.esprit.market_3a33.services.NotificationService;
import tn.esprit.market_3a33.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private int userCIN = 87654321; // Store the CIN of the logged-in user
    @FXML
    private FlowPane productContainer;
    @FXML
    private ListView<String> cartListView;  // Main page cart view (displays text details)
    @FXML
    private Label totalPriceLabel;
    @FXML
    private ListView<String> notificationListView;
    @FXML
    private VBox notificationBox; // Reference to the notification VBox
    @FXML
    private ScrollPane mainScrollPane; // Reference to the main ScrollPane

    @FXML
    private void toggleNotifications() {
        // Toggle visibility of the notification section
        boolean isVisible = notificationBox.isVisible();
        notificationBox.setVisible(!isVisible);
        notificationBox.setManaged(!isVisible);

        // Scroll to the top if notifications are made visible
        if (!isVisible) {
            mainScrollPane.setVvalue(0);
        }
    }

    private List<Product> products = new ArrayList<>();
    private double totalPrice = 0.0;
    private List<Product> cartProducts = new ArrayList<>(); // Holds selected products

    @FXML
    public void initialize() {
        // Check for confirmed orders for the logged-in user
        checkForConfirmedOrders();

        // Load products and display them
        loadProductsFromDatabase();
        displayProducts();
    }

    private void checkForConfirmedOrders() {
        // Query to fetch confirmed orders for the logged-in user
        String query = "SELECT id FROM orders WHERE userId = ? AND status = 'Confirmed'";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userCIN);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("id");

                // Calculate the total price for this order by summing up the priceTotal of all orderItems
                double totalPrice = calculateTotalPriceForOrder(orderId);

                // Add a notification for each confirmed order
                String notification = "Order #" + orderId + " has been confirmed. Total: " + String.format("%.2f dt", totalPrice);
                receiveNotification(notification);

                // Show a notification alert
                NotificationService.showNotification(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double calculateTotalPriceForOrder(int orderId) {
        String query = "SELECT SUM(priceTotal) AS totalPrice FROM orderItems WHERE orderId = ?";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("totalPrice"); // Return the sum of priceTotal for the order
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0 if no orderItems are found or an error occurs
    }

    /**
     * Adds a notification to the notification list.
     *
     * @param message The notification message.
     */
    public void receiveNotification(String message) {
        // Ensure the notificationListView is initialized
        if (notificationListView != null) {
            notificationListView.getItems().add(message);
        }
    }

    @FXML
    private void handleNotificationClick() {
        String selectedNotification = notificationListView.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            showOrderDetailsModal(selectedNotification);
        }
    }

    /**
     * Displays a modal with order details.
     *
     * @param notification The notification message.
     */
    private void showOrderDetailsModal(String notification) {
        try {
            // Extract the orderId from the notification message
            int orderId = extractOrderIdFromNotification(notification);

            // Load the modal FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/market_3a33/orderDetailsModal.fxml"));
            Parent root = loader.load();

            // Pass the notification message and orderId to the modal controller
            OrderDetailsModalController modalController = loader.getController();
            modalController.setNotification(notification);
            modalController.setOrderId(orderId);

            // Create and show the modal
            Stage stage = new Stage();
            stage.setTitle("Order Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make the modal block other windows

            // Refresh notifications after the modal is closed
            stage.setOnHidden(event -> refreshNotifications());

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int extractOrderIdFromNotification(String notification) {
        // Assuming the notification message is in the format: "Order #123 has been confirmed. Total: 100.00 dt"
        String[] parts = notification.split(" ");
        if (parts.length > 1) {
            String orderIdStr = parts[1].replace("#", "");
            return Integer.parseInt(orderIdStr);
        }
        return -1; // Return -1 if the orderId cannot be extracted
    }

    private void refreshNotifications() {
        // Clear the current notifications
        notificationListView.getItems().clear();

        // Re-fetch confirmed orders and update the notification list
        checkForConfirmedOrders();
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