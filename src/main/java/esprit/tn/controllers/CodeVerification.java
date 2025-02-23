package esprit.tn.controllers;

import esprit.tn.services.authentificationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CodeVerification {

    @FXML private TextField codeField;
    @FXML private Label errorLabel;
    @FXML private Button verifyButton;

    private final authentificationService authService = new authentificationService();
    private String userEmail; // Store the email of the user

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void handleVerifyCode(ActionEvent event) {
        String enteredCode = codeField.getText().trim();

        if (enteredCode.isEmpty()) {
            errorLabel.setText("⚠ Please enter the verification code.");
            return;
        }

        // Check if userEmail is null or empty
        if (userEmail == null || userEmail.isEmpty()) {
            errorLabel.setText("❌ Error: No email associated with this request.");
            System.out.println("[ERROR] userEmail is null or empty.");
            return;
        }

        System.out.println("[INFO] Checking stored code for email: " + userEmail);

        // Fetch stored code
        String storedCode = authService.getStoredVerificationCode(userEmail);

        if (storedCode == null) {
            errorLabel.setText("❌ No verification code found. Request a new one.");
            System.out.println("[ERROR] No verification code found for email: " + userEmail);
            return;
        }

        System.out.println("[INFO] Entered code: " + enteredCode + ", Stored code: " + storedCode);

        if (enteredCode.equals(storedCode)) {
            errorLabel.setText("✅ Code verified! Redirecting...");
            errorLabel.setStyle("-fx-text-fill: green;");

            // Redirect to Reset Password Screen
            openResetPasswordScreen(event);
        } else {
            errorLabel.setText("❌ Invalid code. Please try again.");
            System.out.println("[ERROR] Incorrect verification code entered.");
        }
    }


    private void openResetPasswordScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();

            // Pass user email to the reset password controller
            ResetPasswordController resetController = loader.getController();
            resetController.setUserEmail(userEmail);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
