package tn.esprit.market_3a33.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.market_3a33.entities.Product;

import java.util.List;

public class CartController {
    @FXML
    private TableView<Product> cartTable;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private Button payButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Product> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Ensure the TableColumns are bound to the correct property names
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTable.setItems(cartItems);
    }

    // This method is called from MainController to populate the cart page.
    public void setCartData(List<Product> cartProducts, double totalPrice) {
        cartItems.setAll(cartProducts);
        // Refresh the table so the items are shown
        cartTable.refresh();
    }

    @FXML
    private void handleDeleteOrder() {
        Product selectedProduit = cartTable.getSelectionModel().getSelectedItem();
        if (selectedProduit != null) {
            cartItems.remove(selectedProduit);
        }
    }

    @FXML
    private void handlePayOrder() {
        System.out.println("Proceeding to payment...");
        // Implement payment logic here.
    }
}
