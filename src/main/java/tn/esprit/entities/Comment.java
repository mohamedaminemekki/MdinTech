package tn.esprit.entities;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int postId; // Ajouter ce champ
    private String content;
    private String authorCin; // Référence au CIN de l'utilisateur
    private LocalDateTime createdAt;

    public Comment() {
        super();

    }

    public Comment(int id, String content, String authorCin, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.authorCin = authorCin;
        this.createdAt = createdAt;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorCin() {
        return authorCin;
    }

    public void setAuthorCin(String authorCin) {
        this.authorCin = authorCin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public int getPostId() {
        return postId;
    }
    public void setPostId(int postId) {
        this.postId = postId;
    }
}


