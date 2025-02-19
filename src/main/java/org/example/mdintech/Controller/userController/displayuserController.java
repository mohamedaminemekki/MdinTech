package org.example.mdintech.Controller.userController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.example.mdintech.entities.User;
import org.example.mdintech.service.userService;

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

        // Custom ListView Cell Renderer
        usersList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<>() {
                    private final ImageView profileImageView = new ImageView();

                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);

                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(user.getName() + " - " + user.getPhone() + " - " + user.getEmail() + " - CIN: " + user.getCIN());

                            // Dummy profile image (Replace with real image if available)
                            profileImageView.setImage(new Image("file:src/main/resources/profile_placeholder.png"));
                            profileImageView.setFitWidth(40);
                            profileImageView.setFitHeight(40);
                            setGraphic(profileImageView);
                        }
                    }
                };
            }
        });

        // Handle user selection
        usersList.setOnMouseClicked(event -> {
            User selectedUser = usersList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                showUserDetailsPopup(selectedUser);
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
}
