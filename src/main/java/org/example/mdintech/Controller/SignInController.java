package org.example.mdintech.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.mdintech.entities.User;
import org.example.mdintech.service.NotificationModule.mailNotificationService;
import org.example.mdintech.utils.PasswordVerification;
import org.example.mdintech.utils.UserRole;
import org.example.mdintech.service.userService;
import org.example.mdintech.utils.navigation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

public class SignInController {

    @FXML
    private TextField nameField, cinField, emailField, phoneField, addressField, cityField, stateField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Label passwordStrengthLabel;

    @FXML
    private ImageView profileImageView;

    private File selectedImageFile;


    private final userService userService = new userService(); // Service for saving users

    @FXML
    private void handleSignIn(ActionEvent event) { // Add ActionEvent parameter
        try {
            String name = nameField.getText();
            int cin = Integer.parseInt(cinField.getText());
            String email = emailField.getText();
            String password = passwordField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String city = cityField.getText();
            String state = stateField.getText();
            UserRole role = UserRole.USER;
            Date birthday=new Date(System.currentTimeMillis());

            if (!PasswordVerification.isStrongPassword(password)) {
                showAlert("Weak Password", "Password must be at least 8 characters long, contain at least one uppercase letter, " +
                        "one lowercase letter, one number, and one special character.");
                return;
            }

            String profileImagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "default.png";

            String verificationCode = generateVerificationCode();
            mailNotificationService mailService = new mailNotificationService();
            mailService.sendEmail(email, "Verification Code", "Your verification code is: " + verificationCode);

            String userEnteredCode = showVerificationPopup();
            if (userEnteredCode == null || !userEnteredCode.equals(verificationCode)) {
                showAlert("Error", "Incorrect verification code. Please try again.");
                return;
            }

            User newUser = new User(name, cin, email, password, role, phone, address, city, state,profileImagePath,birthday);
            userService.save(newUser);
            showAlert("Success", "User registered successfully!");

            navigation.switchScene(event, "/org/example/mdintech/userModule/login-view.fxml");

        } catch (NumberFormatException e) {
            showAlert("Error", "CIN must be a valid number!");
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String showVerificationPopup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Email Verification");
        dialog.setHeaderText("A verification code has been sent to your email.");
        dialog.setContentText("Enter the code:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null); // Returns null if the user cancels
    }


    private void goToDashboard(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void checkPasswordStrength() {
        String password = passwordField.getText();
        if (PasswordVerification.isStrongPassword(password)) {
            passwordStrengthLabel.setText("Strong password.");
            passwordStrengthLabel.setStyle("-fx-text-fill: green;");
        } else {
            passwordStrengthLabel.setText("Weak password! Must have at least 8 characters, one uppercase, one lowercase, one number, and one special character.");
            passwordStrengthLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void handleBackButton(ActionEvent event) throws IOException {
        navigation.switchScene(event, "/org/example/mdintech/userModule/login-view.fxml");
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String destinationDir = "C:\\Users\\amine\\Desktop\\PIDEV\\Mdintech\\assets\\ProfileImages";
                File destFolder = new File(destinationDir);

                if (!destFolder.exists()) {
                    destFolder.mkdirs();
                }

                File destFile = new File(destFolder, file.getName());

                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                selectedImageFile = destFile;

                Image image = new Image(destFile.toURI().toString());
                profileImageView.setImage(image);

                showAlert("Success", "Profile image selected successfully!");

            } catch (IOException e) {
                showAlert("Error", "Failed to save the image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a 6-digit number
        return String.valueOf(code);
    }
}
