package org.example.mdintech.Singleton;

import org.example.mdintech.entities.User;

public class loggedInUser {
    private static loggedInUser instance;
    private User loggedUser;

    private loggedInUser(User user) {
        this.loggedUser = user;
    }

    public static void initializeSession(User user) {
        if (instance == null) {
            instance = new loggedInUser(user);
        }
    }

    public static loggedInUser getInstance() {
        return instance;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public static void clearSession() {
        instance = null;
    }
}
