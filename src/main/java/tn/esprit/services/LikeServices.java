package tn.esprit.services;

import tn.esprit.entities.Like;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LikeServices {
    private final Connection con = MyDatabase.getInstance().getCon();

    public void toggleLike(String userCin, int postId) throws SQLException {
        if (hasLiked(userCin, postId)) {
            String deleteQuery = "DELETE FROM likes WHERE user_cin = ? AND post_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteQuery)) {
                ps.setString(1, userCin);
                ps.setInt(2, postId);
                ps.executeUpdate();
            }
        } else {
            String insertQuery = "INSERT INTO likes (user_cin, post_id, created_at) VALUES (?, ?, ?)"; // Added created_at
            try (PreparedStatement ps = con.prepareStatement(insertQuery)) {
                ps.setString(1, userCin);
                ps.setInt(2, postId);
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // Set current time
                ps.executeUpdate();
            }
        }
    }
    public List<Like> getLikesForPost(int postId) throws SQLException {
        List<Like> likes = new ArrayList<>();
        String query = "SELECT * FROM likes WHERE post_id = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                likes.add(new Like(
                        rs.getInt("post_id"), // Use ResultSet's value
                        rs.getString("user_cin"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return likes;
    }

    private boolean hasLiked(String userCin, int postId) throws SQLException {
        String query = "SELECT COUNT(*) FROM likes WHERE user_cin = ? AND post_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, userCin);
            ps.setInt(2, postId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
