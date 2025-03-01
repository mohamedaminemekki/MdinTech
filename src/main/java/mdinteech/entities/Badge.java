package mdinteech.entities;

public class Badge {
    private String id;
    private String description;
    private String imagePath;

    public Badge(String id, String description, String imagePath) {
        this.id = id;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }
}
