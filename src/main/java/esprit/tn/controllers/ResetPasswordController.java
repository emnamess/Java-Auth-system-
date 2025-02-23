package esprit.tn.controllers;

import esprit.tn.services.authentificationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ResetPasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final authentificationService authService = new authentificationService();
    private String userEmail;

    // Set the email of the user who is resetting the password
    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Check if email is set
        if (userEmail == null || userEmail.isEmpty()) {
            messageLabel.setText("⚠ No user email found. Try again.");
            return;
        }

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

        // Update password in database
        boolean success = authService.updatePassword(userEmail, newPassword);
        if (success) {
            messageLabel.setText("✅ Password reset successful! You can now log in.");
            messageLabel.setStyle("-fx-text-fill: green;");
            closeWindow();
        } else {
            messageLabel.setText("❌ Failed to reset password. Try again.");
            newPasswordField.clear();
            confirmPasswordField.clear();
        }
    }

    // Password validation method (at least 8 characters, one number, one special character)
    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*\\d.*") && // At least one number
                password.matches(".*[!@#$%^&*()].*"); // At least one special character
    }

    private void closeWindow() {
        Stage stage = (Stage) newPasswordField.getScene().getWindow();
        stage.close();
    }
}
