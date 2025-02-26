package org.example.mdintech.Controller.userController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.example.mdintech.entities.User;
import org.example.mdintech.service.userService;
import org.example.mdintech.utils.navigation;

import java.io.IOException;
import java.util.List;

public class displayuserController {

    @FXML
    private ListView<User> usersList;

    private userService us = new userService();

    @FXML
    public void initialize() {
        loadUsers();
    }

    @FXML
    public void loadUsers() {
        List<User> users = us.findAll();
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        usersList.setItems(observableUsers);

        // Custom ListView Cell Renderer with Block/Unblock button
        usersList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<>() {
                    private final ImageView profileImageView = new ImageView();
                    private final Button blockButton = new Button();

                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);

                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(user.getName() + " - " + user.getPhone() + " - " + user.getEmail() + " - CIN: " + user.getCIN());

                            // Profile Image Placeholder
                            profileImageView.setImage(new Image("file:src/main/resources/profile_placeholder.png"));
                            profileImageView.setFitWidth(40);
                            profileImageView.setFitHeight(40);

                            // Configure Block/Unblock Button
                            updateBlockButton(user);

                            // Button Click Action
                            blockButton.setOnAction(event -> toggleUserStatus(user));

                            setGraphic(profileImageView);
                            setGraphic(blockButton);
                        }
                    }

                    // Updates button text and style based on user's status
                    private void updateBlockButton(User user) {
                        if (user.isStatus()) {
                            blockButton.setText("Block");
                            blockButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        } else {
                            blockButton.setText("Unblock");
                            blockButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                        }
                    }

                    // Toggle user status and update in DB
                    private void toggleUserStatus(User user) {
                        boolean newStatus = !user.isStatus();
                        user.setStatus(newStatus); // Update the local object

                        us.updateUserStatus(user.getCIN(), newStatus); // Update DB
                        updateBlockButton(user); // Refresh button
                    }
                };
            }
        });
    }

    private void showUserDetailsPopup(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Details");
        alert.setHeaderText("Information of " + user.getName());
        alert.setContentText(
                "CIN: " + user.getCIN() + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "Phone: " + user.getPhone() + "\n" +
                        "Address: " + user.getAddress() + "\n" +
                        "City: " + user.getCity() + "\n" +
                        "State: " + user.getState() + "\n" +
                        "Role: " + user.getRole() + "\n" +
                        "Status: " + (user.isStatus() ? "Active" : "Inactive")
        );
        alert.showAndWait();
    }
    public void handleBackButton(ActionEvent event) throws IOException {
        navigation.switchScene(event, "/org/example/mdintech/main-admin-view.fxml");
    }
}
