package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import entities.RendezVous;
import services.RendezVousServices;
import services.MedecinServices;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.SQLException;
import javafx.event.ActionEvent;

public class RendezVousController {

    @FXML
    private ComboBox<String> salleComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Spinner<Integer> heureSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private Button confirmButton;

    private MedecinController.MedecinItem medecin;
    private RendezVousServices rendezVousService;
    private MedecinServices medecinService;

    public void showRendezVousForm(MedecinController.MedecinItem medecin) {
        try {
            this.medecin = medecin;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rendezvous_form.fxml"));
            VBox ROOT = loader.load();
            Scene scene = new Scene(ROOT);

            RendezVousController controller = loader.getController();
            controller.initializeForm(medecin);

            Stage stage = new Stage();
            stage.setTitle("Formulaire de Rendez-vous");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        rendezVousService = new RendezVousServices();
        medecinService = new MedecinServices();

        SpinnerValueFactory<Integer> heureFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        heureSpinner.setValueFactory(heureFactory);

        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteSpinner.setValueFactory(minuteFactory);

        salleComboBox.getItems().addAll("Salle A", "Salle B", "Salle C");

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ff0000;");
                }
            }
        });
    }

    public void initializeForm(MedecinController.MedecinItem medecin) {
        this.medecin = medecin;
    }

    @FXML
    public void handleConfirmRendezVous(ActionEvent event) {
        try {
            String salle = salleComboBox.getValue();
            LocalDate date = datePicker.getValue();
            int heure = heureSpinner.getValue();
            int minute = minuteSpinner.getValue();

            if (salle == null || date == null) {
                throw new IllegalArgumentException("Tous les champs doivent être remplis !");
            }

            LocalTime time = LocalTime.of(heure, minute);

            // Vérification de la disponibilité du créneau
            if (rendezVousService.isTimeSlotTaken(date, time)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Créneau Indisponible");
                alert.setHeaderText("Ce créneau est déjà réservé.");
                alert.setContentText("Veuillez choisir une autre heure.");
                alert.showAndWait();
                return;
            }

            RendezVous rendezVous = new RendezVous();
            rendezVous.setLieu(salle);
            rendezVous.setDateRendezVous(date);
            rendezVous.setTimeRendezVous(time);
            rendezVous.setStatus("confirmé");
            rendezVous.setIdMedecin(medecin.getId());

            rendezVousService.add(rendezVous);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Rendez-vous confirmé avec succès !");
            successAlert.showAndWait();

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        } catch (SQLException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur Base de Données");
            errorAlert.setHeaderText("Erreur lors de l'insertion du rendez-vous.");
            errorAlert.showAndWait();
            e.printStackTrace();
        } catch (Exception e) {
            Alert generalAlert = new Alert(Alert.AlertType.ERROR);
            generalAlert.setTitle("Erreur Inattendue");
            generalAlert.setHeaderText("Une erreur est survenue.");
            generalAlert.showAndWait();
            e.printStackTrace();
        }
    }


}
