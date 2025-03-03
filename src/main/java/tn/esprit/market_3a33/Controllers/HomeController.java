package tn.esprit.market_3a33.Controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import tn.esprit.market_3a33.entities.Product;
import tn.esprit.market_3a33.services.ProductService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController {

    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label revenueLabel;
    @FXML
    private GridPane productsGrid;
    @FXML
    private BarChart<String, Number> productsSoldChart; // BarChart for products sold

    private ProductService productDAO = new ProductService();

    @FXML
    public void initialize() {
        // Fetch data
        int totalProducts = productDAO.getTotalProducts();
        int totalOrders = productDAO.getTotalOrders();
        int lowStockProducts = productDAO.getLowStockProductsNumber();
        double totalRevenue = productDAO.getTotalRevenue();
        List<Product> products = productDAO.getAllProducts();

        // Update cards
        totalProductsLabel.setText(String.valueOf(totalProducts));
        totalOrdersLabel.setText(String.valueOf(totalOrders));
        lowStockLabel.setText(String.valueOf(lowStockProducts));
        revenueLabel.setText(String.format("%.2fdt", totalRevenue));

        // Update products grid
        int rowIndex = 1; // Start from row 1 (row 0 is for headers)
        for (Product product : products) {
            productsGrid.add(new Label(product.getName()), 0, rowIndex);
            productsGrid.add(new Label(String.valueOf(product.getStock())), 1, rowIndex);
            productsGrid.add(new Label(String.format("%.2fdt", product.getPrice())), 2, rowIndex);
            rowIndex++;
        }

        // Populate the products sold chart with top 6 products
        populateProductsSoldChart(products);
    }

    private void populateProductsSoldChart(List<Product> products) {
        // Sort products by soldCount in descending order and limit to top 6
        List<Product> topProducts = products.stream()
                .sorted(Comparator.comparingInt(Product::getSold).reversed())
                .limit(6)
                .collect(Collectors.toList());

        // Create a series for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Product product : topProducts) {
            series.getData().add(new XYChart.Data<>(product.getName(), product.getSold()));
        }

        // Add the series to the chart
        productsSoldChart.getData().add(series);

        // Customize the chart appearance
        productsSoldChart.setCategoryGap(20); // Add space between bars
        productsSoldChart.setAnimated(true); // Enable animations

        // Add product names under each bar
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #3498DB;"); // Blue color for bars
            Tooltip tooltip = new Tooltip(String.format("Sold: %d", data.getYValue().intValue()));
            Tooltip.install(data.getNode(), tooltip);
        }
    }
}