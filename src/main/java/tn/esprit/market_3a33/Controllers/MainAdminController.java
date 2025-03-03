package tn.esprit.market_3a33.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainAdminController {

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox homeSection;

    @FXML
    private VBox productManagementSection;

    @FXML
    private VBox orderManagementSection;

    @FXML
    private VBox stockManagementSection;

    @FXML
    private void loadHome() {
        homeSection.setVisible(true);
        productManagementSection.setVisible(false);
        orderManagementSection.setVisible(false);
        stockManagementSection.setVisible(false);
    }

    @FXML
    private void loadProductManagement() {
        homeSection.setVisible(false);
        productManagementSection.setVisible(true);
        orderManagementSection.setVisible(false);
        stockManagementSection.setVisible(false);
    }

    @FXML
    private void loadOrderManagement() {
        homeSection.setVisible(false);
        productManagementSection.setVisible(false);
        orderManagementSection.setVisible(true);
        stockManagementSection.setVisible(false);
    }

    @FXML
    private void loadStockManagement() {
        homeSection.setVisible(false);
        productManagementSection.setVisible(false);
        orderManagementSection.setVisible(false);
        stockManagementSection.setVisible(true);
    }
}