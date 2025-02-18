package tn.esprit.market_3a33.entities;

public class Produit {
    private int id;
    private String name;
    private double price;
    private String imagePath;

    public Produit(int id, String name, double price, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }
}
