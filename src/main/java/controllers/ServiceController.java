package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ServiceHospitalierServices;
import entities.ServiceHospitalier;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ServiceController {

    @FXML
    private ListView<ServiceItem> serviceListView;
    @FXML
    private Button btnMesRendezVous;
    @FXML
    private TextField searchField;

    @FXML
    private void onSearch() {
        String searchText = searchField.getText().trim();
        searchService(searchText);
    }


    private final ServiceHospitalierServices serviceHospitalierServices = new ServiceHospitalierServices();

    @FXML
    public void initialize() {
        loadServicesFromDatabase();


        // Ajouter l'événement de clic sur un service
        serviceListView.setOnMouseClicked(event -> {
            ServiceItem selectedService = serviceListView.getSelectionModel().getSelectedItem();
            if (selectedService != null) {
                // Récupérer l'ID du service sélectionné
                int idService = getServiceIdFromName(selectedService.getName());
                if (idService != 0) {  // Vérifie que l'ID est valide
                    // Appeler la méthode pour afficher les médecins de ce service
                    showMedecinsForService(idService);
                } else {
                    System.err.println("Service non trouvé !");
                }
            }
        });
    }

    private void loadServicesFromDatabase() {
        ObservableList<ServiceItem> services = FXCollections.observableArrayList();

        try {
            // Récupérer la liste des services depuis la base de données
            List<ServiceHospitalier> serviceList = serviceHospitalierServices.readList();

            // Convertir en objets `ServiceItem` et ajouter à l'ObservableList
            for (ServiceHospitalier s : serviceList) {
                String imageUrl = getImagePathForService(s.getNomService()); // Obtenir l'image spécifique
                services.add(new ServiceItem(s.getNomService(), s.getDescription(), imageUrl));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des services: " + e.getMessage());
        }

        serviceListView.setItems(services);
        serviceListView.setCellFactory(listView -> new ServiceListCell());
    }

    // Méthode pour retourner le chemin de l'image selon le nom du service
    private String getImagePathForService(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "neurologie":
                return "/images/Neuro.jpg";
            case "pédiatrie":
                return "/images/pediatrie.jpg";
            case "gynécologie":
                return "/images/genico.jpg";
            case "orthopédie":
                return "/images/ortho.jpg";
            case "dermatologie":
                return "/images/derma.jpg";
            case "cardiologie":
                return "/images/cardio.jpg";
            default:
                return "/images/default.jpg"; // Image par défaut si le service n'est pas reconnu
        }
    }

    // Méthode pour obtenir l'ID du service à partir de son nom
    private int getServiceIdFromName(String serviceName) {
        try {
            return serviceHospitalierServices.getServiceIdFromName(serviceName);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'ID du service : " + e.getMessage());
            return -1; // Valeur d'erreur
        }
    }

    // Méthode pour afficher les médecins du service
    private void showMedecinsForService(int idService) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/medecin_list.fxml"));
            AnchorPane medecinListView = loader.load();
            MedecinController controller = loader.getController();
            controller.loadMedecinsForService(idService);
            Stage stage = new Stage();
            stage.setTitle("Médecins spécialisés");
            stage.setScene(new Scene(medecinListView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe interne pour représenter un service
    public static class ServiceItem {
        private final String name;
        private final String description;
        private final String imageUrl;

        public ServiceItem(String name, String description, String imageUrl) {
            this.name = name;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    // Classe interne pour personnaliser chaque cellule de la ListView
    private static class ServiceListCell extends ListCell<ServiceItem> {
        private final HBox content;
        private final ImageView imageView;
        private final VBox textContainer;
        private final Text name;
        private final Text description;

        public ServiceListCell() {
            imageView = new ImageView();
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            name = new Text();
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            description = new Text();
            description.setStyle("-fx-font-size: 12px;");

            textContainer = new VBox(name, description);
            textContainer.setSpacing(5);

            content = new HBox(imageView, textContainer);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(ServiceItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                try {
                    Image image = new Image(getClass().getResource(item.getImageUrl()).toExternalForm());
                    imageView.setImage(image);
                    name.setText(item.getName());
                    description.setText(item.getDescription());
                    setGraphic(content);
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image : " + item.getImageUrl());
                    e.printStackTrace();
                }
            }
        }
    }

    // Méthode pour ouvrir la vue des rendez-vous
    @FXML
    private void openMesRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rendezvous-view.fxml"));
            AnchorPane rendezvousView = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mes Rendez-vous");
            stage.setScene(new Scene(rendezvousView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchService(String searchText) {
        ObservableList<ServiceItem> filteredServices = FXCollections.observableArrayList();

        try {
            // Récupérer tous les services depuis la base de données
            List<ServiceHospitalier> serviceList = serviceHospitalierServices.readList();

            // Filtrer les services dont le nom correspond à la recherche
            for (ServiceHospitalier s : serviceList) {
                if (s.getNomService().toLowerCase().contains(searchText.toLowerCase())) {
                    String imageUrl = getImagePathForService(s.getNomService());
                    filteredServices.add(new ServiceItem(s.getNomService(), s.getDescription(), imageUrl));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des services: " + e.getMessage());
        }

        // Mettre à jour la liste avec les résultats filtrés
        serviceListView.setItems(filteredServices);
    }

}
