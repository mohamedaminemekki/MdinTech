package tn.esprit.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlogPost {
    private int id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private String imageUrl;
    private boolean approved;
    private List<Comment> comments = new ArrayList<>();
    private List<Like> likes = new ArrayList<>();
    private String title;
    private LocalDate postDate;
    private String category;
    private String authorAvatarUrl;
    private String author_cin;


    // Ajouter getter/setter
    public String getAuthorAvatarUrl() { return authorAvatarUrl; }
    public void setAuthorAvatarUrl(String authorAvatarUrl) { this.authorAvatarUrl = authorAvatarUrl; }
    // Ajouter les getters/setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getPostDate() { return postDate; }
    public void setPostDate(LocalDate postDate) { this.postDate = postDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BlogPost() {}
    public BlogPost(String title, String content, String author, String category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.createdAt = LocalDateTime.now();

    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }
    public int getLikeCount() { return likes.size(); }
    public int getCommentCount() { return comments.size(); }
    public String getAuthorCin() { return author_cin; }
    public void setAuthor_cin(String author_cin) { this.author_cin = author_cin; }
}