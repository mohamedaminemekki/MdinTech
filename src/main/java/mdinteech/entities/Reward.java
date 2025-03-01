package mdinteech.entities;

public class Reward {
    private final String name;
    private final String description;

    public Reward(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}