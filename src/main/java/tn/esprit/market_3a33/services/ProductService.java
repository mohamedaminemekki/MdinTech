package tn.esprit.market_3a33.services;

import tn.esprit.market_3a33.services.IService;
import tn.esprit.market_3a33.utils.MyDatabase;
import tn.esprit.market_3a33.entities.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService implements IService<Product> {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    @Override
    public List<Product> readList() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.id, p.name, p.reference, p.price, p.stockLimit, COALESCE(s.quantity, 0) AS stock, p.sold " +
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
                int sold = rs.getInt("sold");
                String imagePath = "/tn/esprit/market_3a33/images/product" + id + ".jpg";
                products.add(new Product(id, name, reference, price, stockLimit, stock, imagePath, sold));
            }
        } catch (SQLException e) {
            throw e;
        }
        return products;
    }

    public void incrementSoldCount(int productId) {
        String query = "UPDATE products SET sold = sold + 1 WHERE id = ?";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void add(Product product) throws SQLException {
        String productQuery = "INSERT INTO products (name, reference, price, stockLimit) VALUES (?, ?, ?, ?)";
        String stockQuery = "INSERT INTO stock (productId, quantity) VALUES (?, ?)";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement productStmt = conn.prepareStatement(productQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stockStmt = conn.prepareStatement(stockQuery)) {

            // Insert into products table
            productStmt.setString(1, product.getName());
            productStmt.setString(2, product.getReference());
            productStmt.setDouble(3, product.getPrice());
            productStmt.setInt(4, product.getStockLimit());
            productStmt.executeUpdate();

            // Get the generated product ID
            ResultSet generatedKeys = productStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int productId = generatedKeys.getInt(1);

                // Insert into stock table
                stockStmt.setInt(1, productId);
                stockStmt.setInt(2, product.getStock());
                stockStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void update(Product product) throws SQLException {
        String productQuery = "UPDATE products SET name = ?, reference = ?, price = ?, stockLimit = ? WHERE id = ?";
        String stockQuery = "UPDATE stock SET quantity = ? WHERE productId = ?";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement productStmt = conn.prepareStatement(productQuery);
             PreparedStatement stockStmt = conn.prepareStatement(stockQuery)) {

            // Update products table
            productStmt.setString(1, product.getName());
            productStmt.setString(2, product.getReference());
            productStmt.setDouble(3, product.getPrice());
            productStmt.setInt(4, product.getStockLimit());
            productStmt.setInt(5, product.getId());
            productStmt.executeUpdate();

            // Update stock table
            stockStmt.setInt(1, product.getStock());
            stockStmt.setInt(2, product.getId());
            stockStmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void delete(int productId) throws SQLException {
        String deleteStockQuery = "DELETE FROM stock WHERE productId = ?";
        String deleteProductQuery = "DELETE FROM products WHERE id = ?";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement deleteStockStmt = conn.prepareStatement(deleteStockQuery);
             PreparedStatement deleteProductStmt = conn.prepareStatement(deleteProductQuery)) {

            // Delete from stock table first
            deleteStockStmt.setInt(1, productId);
            deleteStockStmt.executeUpdate();

            // Then delete from products table
            deleteProductStmt.setInt(1, productId);
            deleteProductStmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public String getProductNameById(int productId) {
        String sql = "SELECT name FROM products WHERE id = ?";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Product";
    }

    public List<Product> getLowStockProducts() {
        List<Product> lowStockProducts = new ArrayList<>();
        String query = "SELECT p.id, p.name, p.reference, p.price, p.stockLimit, s.quantity, p.sold " +
                "FROM products p " +
                "JOIN stock s ON p.id = s.productId " +
                "WHERE s.quantity < p.stockLimit";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String reference = rs.getString("reference");
                double price = rs.getDouble("price");
                int stockLimit = rs.getInt("stockLimit");
                int quantity = rs.getInt("quantity");
                int sold = rs.getInt("sold");
                String imagePath = "/tn/esprit/market_3a33/images/product" + id + ".jpg";
                // Create a Product object without imagePath
                Product product = new Product(id, name, reference, price, stockLimit, quantity, imagePath, sold);
                lowStockProducts.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockProducts;
    }

    public void updateStock(int productId, int quantityAdded, String location) {
        String sql = "UPDATE stock SET quantity = quantity + ?, location = ? WHERE productId = ?";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantityAdded);
            pstmt.setString(2, location);
            pstmt.setInt(3, productId);

            pstmt.executeUpdate();
            LOGGER.info("Stock updated successfully for product ID: " + productId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock", e);
        }
    }

    public int getTotalProducts() {
        String query = "SELECT COUNT(*) AS total FROM products";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.id, p.name, p.reference, p.price, p.stockLimit, COALESCE(s.quantity, 0) AS stock, p.sold " +
                "FROM products p " +
                "LEFT JOIN stock s ON p.id = s.productId";

        try (Connection conn = MyDatabase.getCon(); // Use singleton connection
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String reference = rs.getString("reference");
                double price = rs.getDouble("price");
                int stockLimit = rs.getInt("stockLimit");
                int stock = rs.getInt("stock");
                int sold = rs.getInt("sold");
                String imagePath = "/tn/esprit/market_3a33/images/product" + id + ".jpg";
                products.add(new Product(id, name, reference, price, stockLimit, stock, imagePath, sold));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public void addProduct(Product product) {
        String productSql = "INSERT INTO products (name, reference, price, stockLimit) VALUES (?, ?, ?, ?)";
        String stockSql = "INSERT INTO stock (productId, quantity, location) VALUES (?, ?, ?)";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement productStmt = conn.prepareStatement(productSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement stockStmt = conn.prepareStatement(stockSql)) {

            // Insert product
            productStmt.setString(1, product.getName());
            productStmt.setString(2, product.getReference());
            productStmt.setDouble(3, product.getPrice());
            productStmt.setInt(4, product.getStockLimit());
            productStmt.executeUpdate();

            // Get the generated product ID
            ResultSet generatedKeys = productStmt.getGeneratedKeys();
            int productId = -1;
            if (generatedKeys.next()) {
                productId = generatedKeys.getInt(1);
            }

            // Insert stock
            if (productId != -1) {
                stockStmt.setInt(1, productId);
                stockStmt.setInt(2, product.getStock());
                stockStmt.setString(3, "Default Location"); // You can change this to a parameter if needed
                stockStmt.executeUpdate();
            }

            LOGGER.info("Product and stock added successfully: " + product.getName());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding product to database", e);
        }
    }

    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, reference = ?, price = ?, stockLimit = ? WHERE id = ?";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getReference());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockLimit());
            pstmt.setInt(5, product.getId());

            pstmt.executeUpdate();
            LOGGER.info("Product updated successfully: " + product.getName());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product", e);
        }
    }

    public void deleteProduct(int productId) {
        Connection conn = null;
        PreparedStatement deleteStockStmt = null;
        PreparedStatement deleteOrderItemsStmt = null;
        PreparedStatement deleteProductStmt = null;

        try {
            conn = MyDatabase.getCon();
            conn.setAutoCommit(false); // Start a transaction

            // 1. Delete from stock table
            String deleteStockSql = "DELETE FROM stock WHERE productId = ?";
            deleteStockStmt = conn.prepareStatement(deleteStockSql);
            deleteStockStmt.setInt(1, productId);
            deleteStockStmt.executeUpdate();

            // 2. Delete from orderItems table
            String deleteOrderItemsSql = "DELETE FROM orderItems WHERE productId = ?";
            deleteOrderItemsStmt = conn.prepareStatement(deleteOrderItemsSql);
            deleteOrderItemsStmt.setInt(1, productId);
            deleteOrderItemsStmt.executeUpdate();

            // 3. Delete from products table
            String deleteProductSql = "DELETE FROM products WHERE id = ?";
            deleteProductStmt = conn.prepareStatement(deleteProductSql);
            deleteProductStmt.setInt(1, productId);
            deleteProductStmt.executeUpdate();

            conn.commit(); // Commit the transaction
            LOGGER.info("Product and related records deleted successfully: " + productId);

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback the transaction on error
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
            }
            LOGGER.log(Level.SEVERE, "Error deleting product", e);
        } finally {
            MyDatabase.close(conn, deleteStockStmt, null);
            MyDatabase.close(null, deleteOrderItemsStmt, null);
            MyDatabase.close(null, deleteProductStmt, null);
        }
    }

    public int getTotalOrders() {
        int totalOrders = 0;
        String query = "SELECT COUNT(*) AS total FROM `orders`";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalOrders = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalOrders;
    }

    public double getTotalRevenue() {
        double totalRevenue = 0;
        String query = "SELECT SUM(priceTotal) AS total FROM orderitems";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalRevenue = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRevenue;
    }

    public int getLowStockProductsNumber() {
        int lowStockProducts = 0;
        String query = "SELECT COUNT(*) AS total FROM products p JOIN stock s ON p.id = s.productId WHERE s.quantity < p.stockLimit";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                lowStockProducts = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockProducts;
    }

}