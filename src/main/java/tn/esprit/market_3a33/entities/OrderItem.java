package tn.esprit.market_3a33.entities;

public class OrderItem {
    private int id;
    private int productId;
    private int quantity;
    private double priceTotal;
    private int orderId;

    // Constructors, Getters, and Setters
    public OrderItem() {}

    public OrderItem(int id, int productId, int quantity, double priceTotal, int orderId) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.priceTotal = priceTotal;
        this.orderId = orderId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceTotal() { return priceTotal; }
    public void setPriceTotal(double priceTotal) { this.priceTotal = priceTotal; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
}