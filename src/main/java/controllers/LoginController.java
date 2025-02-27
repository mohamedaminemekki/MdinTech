package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.MainFX;
import services.EmailService;
import utils.MyDataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField identifiantField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private MainFX mainApp; // Référence à MainFX pour changer les vues

    public void handleLogin() {
        String identifiant = identifiantField.getText();
        String password = passwordField.getText();

        if (identifiant.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
        } else {
            try {
                String query = "SELECT idUser, role FROM user WHERE identifiant = ? AND mdp = SHA2(?, 256)";
                try (PreparedStatement statement = MyDataBase.getInstance().getCon().prepareStatement(query)) {
                    statement.setString(1, identifiant);
                    statement.setString(2, password);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            String role = resultSet.getString("role");
                            int userId = resultSet.getInt("idUser");

                            if (role.equals("user")) {
                                mainApp.showServiceView();

                                // Récupérer l'email du patient depuis la base de données (simulé ici)
                                String userEmail = getUserEmail(userId);

                                if (userEmail != null) {
                                    sendAppointmentReminder(userEmail);
                                } else {
                                    System.err.println("Aucune adresse email trouvée pour cet utilisateur.");
                                }
                            } else if (role.equals("admin")) {
                                mainApp.showAdminView();
                            }
                        } else {
                            showAlert("Erreur", "Identifiants incorrects.");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur de connexion à la base de données.");
            }
        }
    }

    // Fonction pour récupérer l'email d'un utilisateur depuis la base de données
    private String getUserEmail(int userId) {
        String email = null;
        try {
            String query = "SELECT email FROM user WHERE idUser = ?";
            try (PreparedStatement statement = MyDataBase.getInstance().getCon().prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        email = resultSet.getString("email");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    // Fonction pour envoyer un rappel de rendez-vous
    private void sendAppointmentReminder(String email) {
        String subject = "Rappel de votre rendez-vous";
        String body = "Bonjour,\n\nCeci est un rappel de votre rendez-vous prévu dans une heure.\n\nMerci !";

        EmailService.sendEmail(email, subject, body);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setMainApp(MainFX mainApp) {
        this.mainApp = mainApp;
    }
}
