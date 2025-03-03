package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.MainFX;
import utils.MyDataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField identifiantField; // Changement ici, précédemment 'usernameField'
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private MainFX mainApp; // Référence à MainFX pour changer les vues

    // Méthode pour gérer la connexion
    public void handleLogin() {
        String identifiant = identifiantField.getText();
        String password = passwordField.getText();

        // Vérifier la connexion de l'utilisateur
        if (identifiant.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
        } else {
            try {
                String query = "SELECT role FROM user WHERE identifiant = ? AND mdp = SHA2(?, 256)";
                try (PreparedStatement statement = MyDataBase.getInstance().getCon().prepareStatement(query)) {
                    statement.setString(1, identifiant);
                    statement.setString(2, password);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            String role = resultSet.getString("role");

                            if (role.equals("user")) {
                                mainApp.showServiceView(); // Vue classique utilisateur
                            } else if (role.equals("admin")) {
                                // Simplement afficher un message pour vérifier que l'admin est bien connecté
                                mainApp.showAdminView();
                                // Vous pouvez aussi afficher une vue de test ici si vous voulez :
                                // mainApp.showTestAdminView(); // Remplacer par une vue de test simple
                            }
                        } else {
                            showAlert("Erreur", "Identifiants incorrects.");
                        }
                    }
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur de connexion à la base de données.");
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur inattendue.");
            }
        }
    }


    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Set MainFX dans le contrôleur
    public void setMainApp(MainFX mainApp) {
        this.mainApp = mainApp;
    }
}
