package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.sql.SQLException;
import services.MedecinServices;
import entities.Medecin;

public class ListeMedecinsController {

    @FXML
    private ListView<Medecin> medecinListView;

    private MedecinServices medecinServices = new MedecinServices();

    @FXML
    public void initialize() {
        try {
            medecinListView.getItems().setAll(medecinServices.readList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
