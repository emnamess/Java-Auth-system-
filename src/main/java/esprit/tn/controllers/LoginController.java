package esprit.tn.controllers;

import esprit.tn.entities.user;
import esprit.tn.entities.organisateur;
import esprit.tn.entities.partenaire;
import esprit.tn.entities.participant;
import esprit.tn.services.authentificationService;
import esprit.tn.services.userService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final userService userService = new userService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        user loggedInUser = authenticate(username, password);

        if (loggedInUser != null) {
            messageLabel.setText("Login successful!");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Determine user role and navigate accordingly
            if (loggedInUser instanceof organisateur) {
                loadDashboard("organisateur.fxml");
            } else if (loggedInUser instanceof partenaire) {
                loadDashboard("partenaire.fxml");
            } else if (loggedInUser instanceof participant) {
                loadDashboard("participant.fxml");
            } else {
                messageLabel.setText("Unknown user type.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            messageLabel.setText("Invalid username or password.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private user authenticate(String username, String password) {
        // Use userService to get the user by username and password
        return new authentificationService().login(username,password);
    }

    private void loadDashboard(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/views/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading the dashboard.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
