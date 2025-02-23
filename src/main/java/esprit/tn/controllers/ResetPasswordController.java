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

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("⚠ Password fields cannot be empty.");
            return;
        }

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
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) newPasswordField.getScene().getWindow();
        stage.close();
    }
}
