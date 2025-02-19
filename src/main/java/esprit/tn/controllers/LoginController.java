package esprit.tn.controllers;

import esprit.tn.entities.*;
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

        // Check for empty fields
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            messageLabel.setText("⚠ Username and password cannot be empty.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return; // Stop execution if fields are empty
        }

        try {
            user loggedInUser = authenticate(username, password);

            if (loggedInUser != null) {
                messageLabel.setText("✅ Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");

                try {
                    // Determine user role and navigate accordingly
                    if (loggedInUser instanceof organisateur) {
                        loadDashboard("organisateur.fxml");
                    } else if (loggedInUser instanceof partenaire) {
                        loadDashboard("partenaire.fxml");
                    } else if (loggedInUser instanceof participant) {
                        loadDashboard("participant.fxml");
                    } else {
                        messageLabel.setText("⚠ Unknown user type.");
                        messageLabel.setStyle("-fx-text-fill: red;");
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Log error in console
                    messageLabel.setText("❌ Error loading the dashboard: " + e.getMessage());
                    messageLabel.setStyle("-fx-text-fill: red;");
                }
            } else {
                messageLabel.setText("❌ Invalid username or password.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log error for debugging
            messageLabel.setText("❌ Login failed due to an error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }


    private user authenticate(String username, String password) {
        try {
            return new authentificationService().login(username, password);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return null; // Handle it gracefully
        }
    }


    private void loadDashboard(String fxmlFile) {
        try {
            String resourcePath = "/" + fxmlFile;
            System.out.println("Trying to load FXML: " + resourcePath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found at: " + resourcePath);
            }

            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading the dashboard: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
