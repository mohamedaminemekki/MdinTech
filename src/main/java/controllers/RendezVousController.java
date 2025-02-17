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
import java.time.format.DateTimeFormatter;
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
    private int idMedecin; // Déclaration de idMedecin

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

        // Remplir le ComboBox avec la liste des médecins disponibles
        SpinnerValueFactory<Integer> heureFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12); // Valeur par défaut : 12
        heureSpinner.setValueFactory(heureFactory);

        // Configurer le Spinner pour les minutes (0-59)
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0); // Valeur par défaut : 0
        minuteSpinner.setValueFactory(minuteFactory);

        // Ajouter les salles disponibles dans le ComboBox
        salleComboBox.getItems().addAll("Salle A", "Salle B", "Salle C");

        // Exemple de dates disponibles (peut être dynamique selon la logique de votre application)
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Désactiver certaines dates selon la disponibilité
                if (date.isBefore(LocalDate.now()) ||
                        (date.getDayOfMonth() == 25 && date.getMonthValue() == 2 && date.getYear() == 2025) ||
                        (date.getDayOfMonth() == 26 && date.getMonthValue() == 2 && date.getYear() == 2050) ||
                        (date.getDayOfMonth() == 27 && date.getMonthValue() == 2 && date.getYear() == 2025)) {
                    setStyle("-fx-background-color: #ff0000;");
                    setDisable(true); // Désactiver la date
                }
            }
        });
    }

    public void initializeForm(MedecinController.MedecinItem medecin) {
        this.medecin = medecin;
        // On peut ajouter ici des actions supplémentaires, comme pré-remplir les champs
    }

    @FXML
    public void handleConfirmRendezVous(ActionEvent event) {
        try {
            // Récupération et validation des champs
            String salle = salleComboBox.getValue();
            LocalDate date = datePicker.getValue();
            int heure = heureSpinner.getValue();
            int minute = minuteSpinner.getValue();

            // Validation des champs
            if (salle == null || date == null) {
                throw new IllegalArgumentException("Tous les champs doivent être remplis !");
            }

            // Créer un LocalTime à partir des valeurs des Spinner
            LocalTime time = LocalTime.of(heure, minute);

            // Vérifier que le médecin est bien sélectionné
            if (medecin == null) {
                throw new IllegalArgumentException("Aucun médecin sélectionné !");
            }

            // Création et insertion du rendez-vous
            RendezVous rendezVous = new RendezVous();
            rendezVous.setLieu(salle);
            rendezVous.setDateRendezVous(date);
            rendezVous.setTimeRendezVous(time);
            rendezVous.setStatus("confirmé");
            rendezVous.setIdMedecin(medecin.getId()); // Assurez-vous d'utiliser le bon ID du médecin

            // Ajout du rendez-vous dans la base de données
            rendezVousService.add(rendezVous);

            // Afficher un message de confirmation
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