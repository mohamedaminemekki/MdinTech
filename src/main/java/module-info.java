module tn.esprit.market_3a33 {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires javafx.base;
    requires javafx.graphics;
    requires kernel;
    requires layout;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires java.persistence;
    requires stripe.java;

    exports tn.esprit.market_3a33.test;
    opens tn.esprit.market_3a33.test to javafx.graphics, javafx.fxml;
    opens tn.esprit.market_3a33.services to javafx.fxml;
    exports tn.esprit.market_3a33.services;
    opens tn.esprit.market_3a33.utils to javafx.fxml;
    exports tn.esprit.market_3a33.utils;
    opens tn.esprit.market_3a33.Controllers to javafx.fxml;
    opens tn.esprit.market_3a33.entities to javafx.base;

}