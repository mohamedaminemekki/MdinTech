module org.example.mdintech {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires spring.security.crypto;


    opens org.example.mdintech to javafx.fxml;
    exports org.example.mdintech;
}