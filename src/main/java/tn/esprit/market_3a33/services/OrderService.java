package tn.esprit.market_3a33.services;

import tn.esprit.market_3a33.entities.Order;
import tn.esprit.market_3a33.entities.OrderItem;
import tn.esprit.market_3a33.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `orders`";

        try (Connection conn = MyDatabase.getCon();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setDate(rs.getDate("date"));
                order.setStatus(rs.getString("status"));
                order.setUserId(rs.getInt("userId"));

                // Fetch order items
                List<OrderItem> orderItems = getOrderItemsByOrderId(order.getId());
                order.setOrderItems(orderItems);

                orders.add(order);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching orders from database", e);
        }

        return orders;
    }

    private List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM orderItems WHERE orderId = ?";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setProductId(rs.getInt("productId"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPriceTotal(rs.getDouble("priceTotal"));
                item.setOrderId(rs.getInt("orderId"));

                orderItems.add(item);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching order items from database", e);
        }

        return orderItems;
    }

    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE `orders` SET status = ? WHERE id = ?";

        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
            LOGGER.info("Order status updated successfully: " + orderId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating order status in database", e);
        }
    }

    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) AS total FROM `orders`";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving total number of orders", e);
        }
        return 0;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(priceTotal) AS total FROM orderItems";
        try (Connection conn = MyDatabase.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving total revenue", e);
        }
        return 0.0;
    }

    public int saveOrder(Order order, List<OrderItem> orderItems) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;
        int orderId = -1;

        try {
            conn = MyDatabase.getCon();
            conn.setAutoCommit(false); // Start a transaction

            // Insert the order
            String orderSql = "INSERT INTO `orders` (date, status, userId) VALUES (?, ?, ?)";
            orderStmt = conn.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStmt.setDate(1, new java.sql.Date(order.getDate().getTime()));
            orderStmt.setString(2, order.getStatus());
            orderStmt.setInt(3, order.getUserId());
            orderStmt.executeUpdate();

            // Get the generated order ID
            generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);

                // Insert each order item
                String itemSql = "INSERT INTO orderItems (productId, quantity, priceTotal, orderId) VALUES (?, ?, ?, ?)";
                itemStmt = conn.prepareStatement(itemSql);

                for (OrderItem item : orderItems) {
                    itemStmt.setInt(1, item.getProductId());
                    itemStmt.setInt(2, item.getQuantity());
                    itemStmt.setDouble(3, item.getPriceTotal());
                    itemStmt.setInt(4, orderId);
                    itemStmt.addBatch(); // Add to batch for bulk insert
                }

                itemStmt.executeBatch(); // Execute all inserts in one batch
            }

            conn.commit(); // Commit the transaction
            LOGGER.info("Order and items saved successfully. Order ID: " + orderId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving order and items to database", e);
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
                }
            }
        } finally {
            MyDatabase.close(conn, orderStmt, generatedKeys);
            MyDatabase.close(null, itemStmt, null);
        }

        return orderId;
    }

    public void deleteOrder(int orderId) {
        Connection conn = null;
        PreparedStatement deleteOrderItemsStmt = null;
        PreparedStatement deleteOrderStmt = null;

        try {
            conn = MyDatabase.getCon();
            conn.setAutoCommit(false); // Start a transaction

            // 1. Delete order items
            String deleteOrderItemsSql = "DELETE FROM orderItems WHERE orderId = ?";
            deleteOrderItemsStmt = conn.prepareStatement(deleteOrderItemsSql);
            deleteOrderItemsStmt.setInt(1, orderId);
            deleteOrderItemsStmt.executeUpdate();

            // 2. Delete the order
            String deleteOrderSql = "DELETE FROM `orders` WHERE id = ?";
            deleteOrderStmt = conn.prepareStatement(deleteOrderSql);
            deleteOrderStmt.setInt(1, orderId);
            deleteOrderStmt.executeUpdate();

            conn.commit(); // Commit the transaction
            LOGGER.info("Order and associated items deleted successfully: " + orderId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting order and items from database", e);
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
                }
            }
        } finally {
            MyDatabase.close(conn, deleteOrderItemsStmt, null);
            MyDatabase.close(null, deleteOrderStmt, null);
        }
    }
}