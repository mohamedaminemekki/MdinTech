package mdinteech.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import mdinteech.entities.Trip;

import java.io.IOException;

public class TripCell extends ListCell<HBox> {

    private final TripCellController controller;

    public TripCell() {
        // Charger le fichier FXML et initialiser le contrôleur
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mdinteech/views/TripCell2.fxml"));
        controller = new TripCellController();
        loader.setController(controller);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de TripCell2.fxml", e);
        }
    }

    @Override
    protected void updateItem(HBox hbox, boolean empty) {
        super.updateItem(hbox, empty);

        if (empty || hbox == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Récupérer le Trip associé à la HBox
            Trip trip = (Trip) hbox.getUserData();

            // Mettre à jour les informations dans le contrôleur
            controller.setTrip(trip);

            // Appliquer un style personnalisé à la cellule sélectionnée
            controller.setSelected(isSelected());

            // Afficher la cellule
            setText(null);
            setGraphic(controller.getCellRoot());
        }
    }
}