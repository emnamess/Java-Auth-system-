package esprit.tn.controllers;

import esprit.tn.entities.JwtUtils;
import esprit.tn.entities.SessionManager;
import esprit.tn.entities.user;
import esprit.tn.services.authentificationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    private final authentificationService authService = new authentificationService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            messageLabel.setText("⚠ Username and password cannot be empty.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            user authenticatedUser = authenticate(username, password);

            if (authenticatedUser != null && authenticatedUser.getJwtToken() != null) {
                messageLabel.setText("✅ Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");

                // Store token in session
                String token = authenticatedUser.getJwtToken();
                SessionManager.setToken(token);

                // Extract user type from JWT and load dashboard
                String userType = JwtUtils.extractRole(token);
                loadDashboardBasedOnRole(userType);
            } else {
                messageLabel.setText("❌ Invalid username or password.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Login failed due to an error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }


    private void loadDashboardBasedOnRole(String userType) {
        switch (userType) {
            case "organisateur":
                loadDashboard("organisateur.fxml");
                break;
            case "partenaire":
                loadDashboard("partenaire.fxml");
                break;
            case "participant":
                loadDashboard("participant.fxml");
                break;
            case "admin":
                loadDashboard("admin.fxml");
                break;
            default:
                messageLabel.setText("⚠ Unknown user type.");
                messageLabel.setStyle("-fx-text-fill: red;");
                break;
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUser.fxml"));
            Parent signupPage = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(signupPage);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private user authenticate(String username, String password) {
        try {
            return authService.login(username, password); // Return the user object instead of token
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadDashboard(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
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
