package controllers;

import entities.Medecin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import services.MedecinServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.List;

public class MedecinController {

    @FXML
    private ListView<MedecinItem> medecinListView;

    // Méthode appelée pour charger les médecins pour un service spécifique
    public void loadMedecinsForService(int idService) {
        MedecinServices medecinServices = new MedecinServices();
        ObservableList<MedecinItem> medecins = FXCollections.observableArrayList();

        try {
            // Récupérer les médecins pour un service spécifique
            List<Medecin> medecinList = medecinServices.getMedecinsByService(idService);
            System.out.println("Service ID: " + idService);
            if (medecinList.isEmpty()) {
                System.out.println("Aucun médecin trouvé pour ce service.");
            }
            for (Medecin medecin : medecinList) {
                String imagePath = "/images/" + medecin.getNomM().toLowerCase() + ".jpg";  // Image du médecin
                medecins.add(new MedecinItem(medecin.getIdMedecin(),medecin.getNomM(), medecin.getPrenomM(), medecin.getSpecialite(), medecin.getContact(), imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mise à jour de la ListView avec les médecins
        medecinListView.setItems(medecins);
        medecinListView.setCellFactory(listView -> new MedecinListCell());
    }

    // Classe interne pour stocker les informations d'un médecin
    public static class MedecinItem {
        private final int id;
        private final String nom;
        private final String prenom;
        private final String specialite;
        private final int contact;
        private final String imageUrl;

        public MedecinItem(int id,String nom, String prenom, String specialite, int contact, String imageUrl) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.specialite = specialite;
            this.contact = contact;
            this.imageUrl = imageUrl;
        }
        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getSpecialite() { return specialite; }
        public int getContact() { return contact; }
        public String getImageUrl() { return imageUrl; }


    }

    // Classe interne pour personnaliser chaque cellule de la liste
    private static class MedecinListCell extends ListCell<MedecinItem> {
        private final HBox content;
        private final ImageView imageView;
        private final VBox textContainer;
        private final Text nom;
        private final Text specialite;
        private final Text contact;
        private final Button rdvButton;
        private HBox buttonContainer;  // Déclarer la variable ici, sans la réinitialiser dans le constructeur.

        public MedecinListCell() {
            // Initialisation de l'image
            imageView = new ImageView();
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            // Initialisation des textes
            nom = new Text();
            nom.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            specialite = new Text();
            specialite.setStyle("-fx-font-size: 12px;");
            contact = new Text();
            contact.setStyle("-fx-font-size: 12px; -fx-fill: #000000;");

            // Initialisation du conteneur de texte
            textContainer = new VBox(nom, specialite, contact); // Ajout du contact
            textContainer.setSpacing(5);

            // Initialisation du bouton de rendez-vous
            rdvButton = new Button("Rendez-vous");
            rdvButton.setOnAction(event -> {
                // Code pour ouvrir la fenêtre de rendez-vous
                openRendezVousForm(getItem());
            });

            // Initialisation du conteneur principal qui combine image et texte
            content = new HBox(imageView, textContainer);
            HBox.setHgrow(textContainer, Priority.ALWAYS); // Permet au texte de s'étendre
            content.setSpacing(20);
            content.setAlignment(Pos.CENTER_LEFT);

           // Création d'un conteneur spécifique pour le bouton, aligné à droite
            HBox buttonWrapper = new HBox(rdvButton);
            buttonWrapper.setAlignment(Pos.CENTER_RIGHT);
            buttonWrapper.setPadding(new Insets(0, 10, 0, 0)); // Espacement à droite

            // Ajout du bouton au conteneur principal
            content.getChildren().add(buttonWrapper);

        }

        @Override
        protected void updateItem(MedecinItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                try {
                    Image image = new Image(getClass().getResource(item.getImageUrl()).toExternalForm());
                    imageView.setImage(image);
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image : " + item.getImageUrl());
                    e.printStackTrace();
                    imageView.setImage(null);
                }

                nom.setText(item.getNom() + " " + item.getPrenom());
                specialite.setText(item.getSpecialite());
                contact.setText("Contact: " + item.getContact()); // Affichage du contact

                setGraphic(content);
            }
        }

        private void openRendezVousForm(MedecinItem medecin) {
            System.out.println("ID du médecin sélectionné : " + medecin.getId()); // Vérification
            RendezVousController controller = new RendezVousController();
            controller.showRendezVousForm(medecin);
        }

    }

}

