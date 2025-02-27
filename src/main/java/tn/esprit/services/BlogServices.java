package tn.esprit.services;

import tn.esprit.entities.BlogPost;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlogServices {
    private final Connection con = MyDatabase.getInstance().getCon();

    public void createPost(BlogPost post) throws SQLException {
        String query = "INSERT INTO posts (title, content, author, created_at, image_url, category, approved) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        // On demande à récupérer la clé générée
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getAuthor());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreatedAt()));
            ps.setString(5, post.getImageUrl());
            ps.setString(6, post.getCategory());
            ps.setBoolean(7, post.isApproved());
            ps.executeUpdate();
            // Récupérer l'ID généré et le mettre à jour dans l'objet post
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    post.setId(rs.getInt(1));
                }
            }
        }
    }


    public List<BlogPost> getAllPosts() throws SQLException {
        return getPosts("SELECT * FROM posts ORDER BY created_at DESC");
    }

    public List<BlogPost> getApprovedPosts() throws SQLException {
        String query = "SELECT p.*, u.username as author, u.avatar_url as author_avatar " +
                "FROM posts p " +
                "JOIN users u ON p.author = u.cin " +
                "WHERE p.approved = 1 " +
                "ORDER BY p.created_at DESC";
        return getPosts(query);
    }

    public List<BlogPost> getPendingPosts() throws SQLException {
        return getPosts("SELECT * FROM posts WHERE approved = 0 ORDER BY created_at DESC");
    }

    private List<BlogPost> getPosts(String query) throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                BlogPost post = new BlogPost();
                post.setId(rs.getInt("id"));
                post.setContent(rs.getString("content"));
                post.setAuthor(rs.getString("author"));
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setImageUrl(rs.getString("image_url"));
                post.setApproved(rs.getBoolean("approved"));
                post.setAuthorAvatarUrl(rs.getString("author_avatar"));

                // Charger les commentaires et likes
                post.setComments(new CommentServices().getCommentsForPost(post.getId()));
                post.setLikes(new LikeServices().getLikesForPost(post.getId()));

                posts.add(post);
            }
        }
        return posts;
    }

    public BlogPost getPostById(int postId) throws SQLException {
        String query = "SELECT p.*, u.avatar_url as author_avatar FROM posts p " +
                "JOIN users u ON p.author = u.cin " +
                "WHERE p.id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BlogPost post = new BlogPost();
                post.setId(rs.getInt("id"));
                post.setContent(rs.getString("content"));
                post.setAuthor(rs.getString("author"));
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setImageUrl(rs.getString("image_url"));
                post.setApproved(rs.getBoolean("approved"));
                post.setAuthorAvatarUrl(rs.getString("author_avatar"));
                // Charger les commentaires et les likes pour mettre à jour le nombre
                post.setComments(new CommentServices().getCommentsForPost(post.getId()));
                post.setLikes(new LikeServices().getLikesForPost(post.getId()));
                return post;
            }
        }
        return null;
    }


    public void approvePost(int postId) throws SQLException {
        updateApprovalStatus(postId, true);
    }

    public void rejectPost(int postId) throws SQLException {
        updateApprovalStatus(postId, false);
    }

    private void updateApprovalStatus(int postId, boolean approved) throws SQLException {
        String query = "UPDATE posts SET approved = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setBoolean(1, approved);
            ps.setInt(2, postId);
            System.out.println("Exécution de la requête: " + ps.toString());
            int rowsAffected = ps.executeUpdate();
            System.out.println("Lignes affectées: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour: " + e.getMessage());
            throw e;
        }
    }
    public void deletePost(int postId) throws SQLException {
        String query = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        }
    }

    public void updatePost(BlogPost post) throws SQLException {
        String query = "UPDATE posts SET title = ?, content = ?, image_url = ?, category = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImageUrl());
            ps.setString(4, post.getCategory());
            ps.setInt(5, post.getId());
            ps.executeUpdate();
        }
    }

}
