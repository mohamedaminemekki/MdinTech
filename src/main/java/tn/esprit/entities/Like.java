    package tn.esprit.entities;

    import java.time.LocalDateTime;

    public class Like {
        private int postId;
        private String userCin; // Référence au CIN de l'utilisateur
        private LocalDateTime createdAt;

        // Constructeurs et getters/setters
        public Like(int postId, String userCin, LocalDateTime createdAt){
            this.postId = postId;
            this.userCin = userCin;
            this.createdAt = createdAt;
        }
        public int getPostId() {
            return postId;
        }
        public void setPostId(int postId) {
            this.postId = postId;
        }
        public String getUserCin() {
            return userCin;
        }
        public void setUserCin(String userCin) {
            this.userCin = userCin;
        }
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
