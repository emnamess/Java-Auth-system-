package esprit.tn.controllers;

import esprit.tn.entities.NavigationManager;
import esprit.tn.entities.SessionManager;
import esprit.tn.services.authentificationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResetPasswordController {

    @FXML private PasswordField currentPasswordField; // Only for logged-in users
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private TextField newPasswordTextField;
    @FXML private ImageView toggleNewPassword;

    @FXML private TextField confirmPasswordTextField;
    @FXML private ImageView toggleConfirmPassword;






    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private final authentificationService authService = new authentificationService();
    private String userEmail; // For forgot password
    private Integer userId;   // For logged-in users

    // Set user email (for forgot password flow)
    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    // Set user ID (for profile-based password change)
    public void setUserId(Integer id) {
        this.userId = id;
    }


    @FXML
    private void handleResetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Check if fields are empty
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("⚠ Password fields cannot be empty.");
            return;
        }

        // Validate password strength
        if (!isValidPassword(newPassword)) {
            messageLabel.setText("⚠ Password must be at least 8 chars, with a number & symbol.");
            return;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("❌ Passwords do not match!");
            return;
        }

        boolean success = false;

        // Retrieve userId from token if not already set
        if (userId == null) {
            userId = SessionManager.getUserIdFromToken();
        }

        if (userId != null) {
            // Case 1: Logged-in user changing password
            success = authService.updatePassword1(userId, newPassword);
        } else if (userEmail != null) {
            // Case 2: Forgot password flow
            success = authService.updatePassword(userEmail, newPassword);
        } else {
            messageLabel.setText("⚠ No user found. Try again.");
            return;
        }

        if (success) {
            messageLabel.setText("✅ Password updated successfully!");
            messageLabel.setStyle("-fx-text-fill: green;");
            newPasswordField.clear();
            confirmPasswordField.clear();

            // Use NavigationManager to go back to the previous scene
            if (NavigationManager.hasPreviousScene()) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene previousScene = NavigationManager.popScene();
                stage.setScene(previousScene);
            }
        } else {
            messageLabel.setText("❌ Failed to update password. Try again.");
            newPasswordField.clear();
            confirmPasswordField.clear();
        }
    }


    // Password validation method
    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*\\d.*") ;  // At least one number

    }

    private void closeWindow() {
        Stage stage = (Stage) newPasswordField.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        if (event.getSource() == toggleNewPassword) {
            isNewPasswordVisible = !isNewPasswordVisible;
            toggleFieldVisibility(newPasswordField, newPasswordTextField, isNewPasswordVisible);
        } else if (event.getSource() == toggleConfirmPassword) {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            toggleFieldVisibility(confirmPasswordField, confirmPasswordTextField, isConfirmPasswordVisible);
        }
    }

    private void toggleFieldVisibility(PasswordField passwordField, TextField textField, boolean isVisible) {
        if (isVisible) {
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            textField.setVisible(false);
        }
    }
    @FXML
    private void goBack(MouseEvent event) {
        if (NavigationManager.hasPreviousScene()) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene previousScene = NavigationManager.popScene();
            stage.setScene(previousScene);
        } else {
            System.out.println("No previous scene found.");
        }
    }

}
