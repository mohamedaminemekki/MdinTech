package tn.esprit.market_3a33.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import tn.esprit.market_3a33.entities.Order;
import tn.esprit.market_3a33.entities.OrderItem;
import tn.esprit.market_3a33.entities.Product;
import tn.esprit.market_3a33.services.OrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartController {
    @FXML
    private ListView<String> cartListView;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button deleteButton;

    private ObservableList<String> cartItems = FXCollections.observableArrayList();
    private double totalPrice = 0.0;
    private List<Product> cartProducts = new ArrayList<>();
    private OrderService orderService = new OrderService();

    @FXML
    public void initialize() {
        cartListView.setItems(cartItems);
    }

    // This method is called from MainController to populate the cart page.
    public void setCartData(List<Product> cartProducts, double totalPrice) {
        this.cartProducts = cartProducts;
        this.totalPrice = totalPrice;
        cartItems.clear();
        for (Product product : cartProducts) {
            cartItems.add(product.getName() + " - " + String.format("%.2f dt", product.getPrice()));
        }
        totalPriceLabel.setText(String.format("Total: %.2f dt", totalPrice));
    }

    @FXML
    private void handleDeleteOrder() {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
            cartItems.remove(selectedIndex);
            cartProducts.remove(selectedIndex);
            // Recalculate total price
            totalPrice = cartProducts.stream().mapToDouble(Product::getPrice).sum();
            totalPriceLabel.setText(String.format("Total: %.2f dt", totalPrice));
        }
    }

    @FXML
    private void handleConfirmOrder() {
        if (cartProducts.isEmpty()) {
            System.out.println("Cart is empty. Nothing to confirm.");
            return;
        }

        // Create an Order object
        Order order = new Order();
        order.setDate(new Date());
        order.setStatus("Pending"); // Default status
        order.setUserId(87654321); // Replace with the actual user ID (e.g., from a session)

        // Create OrderItem objects
        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : cartProducts) {
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(1); // Assuming quantity is 1 for each product
            item.setPriceTotal(product.getPrice());
            orderItems.add(item);
        }

        // Save the order and items to the database
        int orderId = orderService.saveOrder(order, orderItems);
        if (orderId != -1) {
            System.out.println("Order confirmed successfully. Order ID: " + orderId);
            // Clear the cart after confirmation
            cartProducts.clear();
            cartItems.clear();
            totalPrice = 0.0;
            totalPriceLabel.setText("Total: 0.00 dt");
        } else {
            System.out.println("Failed to confirm order.");
        }
    }
}