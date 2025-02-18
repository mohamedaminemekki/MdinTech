package tn.esprit.market_3a33.services;

import tn.esprit.market_3a33.entities.Produit;
import tn.esprit.market_3a33.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitServices implements IService<Produit> {

    @Override
    public List<Produit> readList() throws SQLException {
        List<Produit> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection conn = MyDatabase.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String imagePath = "/tn/esprit/market_3a33/images/product" + id + ".jpg";
                products.add(new Produit(id, name, price, imagePath));
            }
        } catch (SQLException e) {
            throw e;
        }
        return products;
    }

    @Override
    public void add(Produit product) throws SQLException {
        String query = "INSERT INTO products (name, price) VALUES (?, ?)";
        try (Connection conn = MyDatabase.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void update(Produit product) throws SQLException {
        String query = "UPDATE products SET name = ?, price = ? WHERE id = ?";
        try (Connection conn = MyDatabase.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void delete(int productId) throws SQLException {
        String query = "DELETE FROM products WHERE id = ?";
        try (Connection conn = MyDatabase.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
