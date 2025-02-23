package esprit.tn.controllers;

import esprit.tn.entities.JwtUtils;
import esprit.tn.entities.SessionManager;
import esprit.tn.entities.user;
import esprit.tn.services.authentificationService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML
    private Button logoutButton;


    private final authentificationService authService = new authentificationService();

    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            messageLabel.setText("⚠ Email and password cannot be empty.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        System.out.println("Attempting login with: " + email + " | " + password);

        try {
            user authenticatedUser = authenticate(email, password);

            if (authenticatedUser != null && authenticatedUser.getJwtToken() != null) {
                String token = authenticatedUser.getJwtToken();
                System.out.println("Generated JWT Token: " + token);

                messageLabel.setText("✅ Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");

                // Store token in session and local file
                SessionManager.setToken(token);
                saveTokenToFile(token);

                String userType = JwtUtils.extractRole(token);
                System.out.println("Extracted user role: " + userType);
                loadDashboard(userType);
            } else {
                messageLabel.setText("❌ Invalid email or password.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Login failed due to an error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Store token in a local file
    private void saveTokenToFile(String token) {
        try (FileWriter writer = new FileWriter("auth_token.txt")) {
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML


    private void initialize() {
        usernameField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) { // Wait until scene is attached
                Platform.runLater(() -> {
                    String savedToken = loadTokenFromFile();
                    if (savedToken != null) {
                        String userType = JwtUtils.extractRole(savedToken);
                        if (userType != null) {
                            System.out.println("✅ Auto-login with saved token.");
                            SessionManager.setToken(savedToken);

                            // Get stage AFTER the scene is attached
                            Stage stage = (Stage) usernameField.getScene().getWindow();
                            if (stage == null) {
                                System.err.println("❌ Error: Stage is still null.");
                                return;
                            }

                            // Call loadDashboard with the correct parameter
                            loadDashboard(userType);
                        }
                    }
                });
            }
        });
    }

    // Load token from local file
    private String loadTokenFromFile() {
        File file = new File("auth_token.txt");
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Page_acceuil.fxml"));
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

    private user authenticate(String email, String password) {
        try {
            return authService.login(email, password); // Return the user object instead of token
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadDashboard(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/"+fxmlFile+".fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();
            if (stage == null) {
                System.err.println("❌ Error: Stage is null.");
                return;
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleforgotpassword(ActionEvent event) {
        String email = usernameField.getText().trim();

        if (email.isEmpty()) {
            messageLabel.setText("⚠ Please enter your email.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check if the email exists in the database
        if (!authService.emailExists(email)) {
            messageLabel.setText("❌ No account found with this email.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Generate a 6-digit verification code
        String verificationCode = String.format("%06d", new java.util.Random().nextInt(999999));

        // Store the code temporarily in the database
        authService.storeVerificationCode(email, verificationCode);

        // Send the verification code email
        boolean emailSent = sendVerificationEmail(email, verificationCode);

        if (emailSent) {
            messageLabel.setText("✅ Verification code sent! Check your email.");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Redirect to the verification code input screen
            openVerificationScreen();
        } else {
            messageLabel.setText("❌ Failed to send email. Try again.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    private void openVerificationScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CodeVerification.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean sendVerificationEmail(String recipientEmail, String verificationCode) {
        final String senderEmail = "messaoudiemna75@gmail.com"; // Replace with your email
        final String senderPassword = "kpof zdho yiic byfp"; // Use an App Password

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Password Reset Code");
            message.setText("Your verification code is: " + verificationCode);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }}
