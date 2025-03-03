package tn.esprit.market_3a33.entities;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private Date date;
    private String status;
    private int userId;
    private List<tn.esprit.market_3a33.entities.OrderItem> orderItems;

    // Constructors, Getters, and Setters
    public Order() {}

    public Order(int id, Date date, String status, int userId, List<tn.esprit.market_3a33.entities.OrderItem> orderItems) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.userId = userId;
        this.orderItems = orderItems;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public List<tn.esprit.market_3a33.entities.OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<tn.esprit.market_3a33.entities.OrderItem> orderItems) { this.orderItems = orderItems; }
}