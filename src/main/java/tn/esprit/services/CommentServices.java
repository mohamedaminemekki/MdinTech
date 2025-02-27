package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.entities.Comment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class CommentServices {
        private final Connection con = MyDatabase.getInstance().getCon();
        public List<Comment> getCommentsForPost(int postId) throws SQLException {
            List<Comment> comments = new ArrayList<>();
            String query = "SELECT * FROM comments WHERE post_id = ?";

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, postId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    comment.setPostId(rs.getInt("post_id")); // Use ResultSet's value
                    comment.setAuthorCin(rs.getString("author_cin"));
                    comment.setContent(rs.getString("content"));
                    comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    comments.add(comment);
                }
            }
            return comments;
        }

    // Dans CommentServices.java
    public void addComment(int postId, String authorCin, String content) throws SQLException {
        String query = "INSERT INTO comments (post_id, author_cin, content, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, postId);
            ps.setString(2, authorCin);
            ps.setString(3, content);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }
    }
    public Comment getCommentById(int commentId) throws SQLException {
        String query = "SELECT * FROM comments WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    comment.setPostId(rs.getInt("post_id"));
                    comment.setAuthorCin(rs.getString("author_cin"));
                    comment.setContent(rs.getString("content"));
                    comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return comment;
                }
            }
        }
        return null;
    }
    public void updateComment(int commentId, String newContent) throws SQLException {
        String query = "UPDATE comments SET content = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newContent);
            ps.setInt(2, commentId);
            ps.executeUpdate();
        }
    }

    public void deleteComment(int commentId) throws SQLException {
        String query = "DELETE FROM comments WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, commentId);
            ps.executeUpdate();
        }
    }

}