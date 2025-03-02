module org.example.mdintech {
    requires javafx.fxml;
    requires java.sql;
    requires spring.security.crypto;
    requires jakarta.mail;
    requires java.dotenv;
    requires commons.validator;
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.auth;

    requires javafx.controls;
    // requires javafx.web;

    // Open the Controller package to javafx.fxml for reflection
    opens org.example.mdintech.Controller to javafx.fxml;
    opens org.example.mdintech.Controller.userController to javafx.fxml;
    opens org.example.mdintech.Controller.parkingController to javafx.fxml;
    opens org.example.mdintech.Controller.userController.parking  to javafx.fxml;



    // Export the main package
    opens org.example.mdintech to javafx.fxml;
    exports org.example.mdintech;
    exports org.example.mdintech.Controller;
}