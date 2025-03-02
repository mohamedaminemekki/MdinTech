package org.example.mdintech.Controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.mdintech.Singleton.loggedInUser;
import org.example.mdintech.entities.User;
import org.example.mdintech.service.NotificationModule.mailNotificationService;
import org.example.mdintech.service.userService;
import org.example.mdintech.utils.UserRole;
import org.example.mdintech.utils.VerificationCodeStorage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class loginController {
    static Dotenv dotenv = Dotenv.load();
    private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
    private static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/userinfo.profile");

    @FXML
    private TextField emailField;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    userService us=new userService();

    @FXML
    public void gotoSignIn(ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mdintech/userModule/sign-in-view.fxml"));
            Parent signInRoot = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(signInRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void login(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        User user=us.login(email, password);
        if (user != null) {
            loggedInUser.initializeSession((user));
            if (user.getRole() == UserRole.ADMIN) {
                goToDashboard(event, "/org/example/mdintech/main-admin-view.fxml");
            } else if (user.getRole() == UserRole.USER) {
                goToDashboard(event, "/org/example/mdintech/main-user-view.fxml");
            }
        }else{
            showAlert("User Not Found ","no credentials are matching the ones you gave us !!!!! .????");
        }
    }

    private void goToDashboard(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardRoot = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void forgotPassword(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Reset Your Password");
        dialog.setContentText("Enter your email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            if (email.isEmpty()) {
                showAlert("Error", "Email field cannot be empty!");
                return;
            }

            if (!us.doesEmailExist(email)) {
                showAlert("Error", "Email not found in our records.");
                return;
            }

            // Generate 6-digit verification code
            String code = generateVerificationCode();

            // Send email
            mailNotificationService mailService = new mailNotificationService();
            mailService.sendEmail(email, "Password Reset Code", "Your verification code is: " + code);

            // Store the generated code somewhere (session, database, temporary cache, etc.)
            VerificationCodeStorage.store(email, code);

            // Redirect to the verification page
            goToVerificationPage(event, email);
        });
    }
    private void goToVerificationPage(ActionEvent event, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mdintech/userModule/verify-code-view.fxml"));
            Parent verificationRoot = loader.load();

            VerificationController controller = loader.getController();
            controller.setEmail(email); // Pass email to verification controller

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(verificationRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Ensures a 6-digit number
        return String.valueOf(code);
    }
//    @FXML
//    private void handleGoogleLogin(ActionEvent event) {
//        // Initialize Google OAuth flow
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                new NetHttpTransport(),
//                GsonFactory.getDefaultInstance(),
//                CLIENT_ID,
//                CLIENT_SECRET,
//                SCOPES)
//                .setAccessType("offline")
//                .build();
//
//        // Generate the authorization URL
//        String authorizationUrl = flow.newAuthorizationUrl()
//                .setRedirectUri(REDIRECT_URI)
//                .build();
//
//        // Open the authorization URL in a WebView
//        WebView webView = new WebView();
//        WebEngine webEngine = webView.getEngine();
//        webEngine.load(authorizationUrl);
//
//        Stage stage = new Stage();
//        stage.setScene(new javafx.scene.Scene(webView, 600, 400));
//        stage.show();
//
//        // Handle the callback
//        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null && newValue.startsWith(REDIRECT_URI)) {
//                String code = newValue.split("code=")[1].split("&")[0];
//                try {
//                    // Exchange the authorization code for tokens
//                    GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
//                            .setRedirectUri(REDIRECT_URI)
//                            .execute();
//
//                    Credential credential = flow.createAndStoreCredential(tokenResponse, null);
//
//                    // Fetch user info
//                    String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + credential.getAccessToken();
//                    String userInfo = new NetHttpTransport().createRequestFactory()
//                            .buildGetRequest(new com.google.api.client.http.GenericUrl(userInfoUrl))
//                            .execute()
//                            .parseAsString();
//
//                    System.out.println("User Info: " + userInfo);
//
//                    // Close the WebView window
//                    stage.close();
//
//                    // TODO: Save user info to your database or create a session
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}
