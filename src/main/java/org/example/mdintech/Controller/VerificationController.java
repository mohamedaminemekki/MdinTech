package org.example.mdintech.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.mdintech.utils.VerificationCodeStorage;

import java.io.IOException;

public class VerificationController {

    @FXML
    private TextField codeField;

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void verifyCode(ActionEvent event) {
        String enteredCode = codeField.getText();
        String storedCode = VerificationCodeStorage.getCode(email);

        if (storedCode != null && storedCode.equals(enteredCode)) {
            VerificationCodeStorage.remove(email); // Remove code after successful verification
            goToResetPasswordPage(event, email);
        } else {
            showAlert("Error", "Invalid verification code. Please try again.");
        }
    }

    private void goToResetPasswordPage(ActionEvent event, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mdintech/userModule/reset-password-view.fxml"));
            Parent resetRoot = loader.load();

            ResetPasswordController controller = loader.getController();
            controller.setEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(resetRoot));
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
}

