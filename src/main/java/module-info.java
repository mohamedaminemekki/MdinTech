module tn.esprit.market_3a33 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires javafx.base;
    requires javafx.graphics;

    exports tn.esprit.market_3a33.test;
    opens tn.esprit.market_3a33.test to javafx.graphics, javafx.fxml;
    opens tn.esprit.market_3a33.services to javafx.fxml;
    exports tn.esprit.market_3a33.services;
    opens tn.esprit.market_3a33.utils to javafx.fxml;
    exports tn.esprit.market_3a33.utils;
    opens tn.esprit.market_3a33.Controllers to javafx.fxml; // 👈 Add this line
    opens tn.esprit.market_3a33.entities to javafx.base;

}