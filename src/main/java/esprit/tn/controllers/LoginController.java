package esprit.tn.controllers;
import esprit.tn.entities.JwtUtils;
import esprit.tn.entities.SessionManager;
import esprit.tn.entities.user;
import esprit.tn.services.authentificationService;
import esprit.tn.services.BlockingService;

import esprit.tn.services.userService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.util.Duration;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginController {
    private static final String CLIENT_SECRET_FILE = "src/main/resources/client_secret.json";
    private static final List<String> SCOPES = Collections.singletonList("email");
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private final int MAX_ATTEMPTS = 3;
    private final long LOCKOUT_DURATION = 15 * 60 ; // 15 minutes in milliseconds
    private int failedAttempts = 0;
    private long lockoutEndTime = 0;
    private final BlockingService blockingService = new BlockingService();

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Label lockoutMessage;
    private Timeline countdownTimer;
    private final authentificationService authService = new authentificationService();
    private final userService users = new userService();
    @FXML private Button loginButton;

    @FXML private Label timerLabel;
    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            messageLabel.setText("⚠ Email and password cannot be empty.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check if user is already blocked
        if (blockingService.isUserBlocked(email)) {
            long blockedUntil = blockingService.getBlockedUntil(email);
            long remainingTime = (blockedUntil - System.currentTimeMillis()) / 1000; // Convert to seconds

            if (remainingTime > 0) {
                messageLabel.setText("⛔ Trop de tentatives. Réessayer dans " + (remainingTime / 60) + " min " + (remainingTime % 60) + " sec.");
                messageLabel.setStyle("-fx-text-fill: red;");
                loginButton.setDisable(true);
                startLockoutTimer(remainingTime); // Start countdown
                return;
            } else {
                blockingService.resetFailedAttempts(email); // Unblock user if time expired
                loginButton.setDisable(false); // Re-enable login button
            }
        }

        try {
            user authenticatedUser = authService.login(email, password);

            if (authenticatedUser != null && authenticatedUser.getJwtToken() != null) {
                blockingService.resetFailedAttempts(email); // ✅ Reset failed attempts on success
                messageLabel.setText("✅ Connexion réussie !");
                messageLabel.setStyle("-fx-text-fill: green;");

                // Proceed to dashboard
                String token = authenticatedUser.getJwtToken();
                SessionManager.setToken(token);
                saveTokenToFile(token);
                loadDashboard(JwtUtils.extractRole(token));
            } else {
                blockingService.incrementFailedAttempts(email); // ❌ Increase failed attempts if login fails

                int failedAttempts = blockingService.getFailedAttempts(email);
                if (failedAttempts >= 3) {
                    long blockedUntil = blockingService.getBlockedUntil(email);
                    long remainingTime = (blockedUntil - System.currentTimeMillis()) / 1000; // Convert to seconds

                    messageLabel.setText("⛔ Trop de tentatives. Réessayer dans " + (remainingTime / 60) + " min " + (remainingTime % 60) + " sec.");
                    messageLabel.setStyle("-fx-text-fill: red;");
                    loginButton.setDisable(true); // Disable login button
                    startLockoutTimer(remainingTime); // Start countdown timer
                } else {
                    messageLabel.setText("❌ Email ou mot de passe incorrect.");
                    messageLabel.setStyle("-fx-text-fill: red;");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Échec de connexion : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private static final int MAX_FAILED_ATTEMPTS = 3;
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

            // Redirect to verification screen and pass email
            openVerificationScreen(email);
        } else {
            messageLabel.setText("❌ Failed to send email. Try again.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    private void openVerificationScreen(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CodeVerification.fxml"));
            Parent root = loader.load();

            // Get controller of the next screen
            CodeVerification controller = loader.getController();
            controller.setUserEmail(email); // Pass the email

            // Open verification screen
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to open Code Verification screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private boolean sendVerificationEmail(String recipientEmail, String verificationCode) {
        final String senderEmail = "messaoudiemna75@gmail.com";
        final String senderPassword = "kpof zdho yiic byfp";

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
    }
    @FXML
    private void handleFaceLogin() {
        new Thread(() -> {
            try {
                String imagePath = captureImage();
                if (imagePath == null) {
                    Platform.runLater(() -> messageLabel.setText("❌ Failed to capture image."));
                    return;
                }

                String userEmail = sendImageToRecognition(imagePath);
                if (userEmail != null) {
                    user existingUser = users.getUserByEmail(userEmail);
                    if (existingUser != null) {
                        // 🔹 Call loginWithFace to generate a JWT token
                        user authenticatedUser = authService.loginWithFace(existingUser.getId_user());

                        if (authenticatedUser != null) {
                            String token = authenticatedUser.getJwtToken();

                            // 🔍 Debugging Logs
                            System.out.println("🔍 User found: " + authenticatedUser.getEmail());
                            System.out.println("🔍 JWT Token Retrieved: " + token);

                            if (token == null || token.trim().isEmpty()) {
                                Platform.runLater(() -> messageLabel.setText("❌ Failed to retrieve JWT token."));
                                return;
                            }

                            SessionManager.setToken(token);
                            System.out.println("🔍 Token stored in session.");

                            String userType = JwtUtils.extractRole(token);
                            Platform.runLater(() -> loadDashboard(userType));
                        } else {
                            Platform.runLater(() -> messageLabel.setText("❌ Failed to authenticate user."));
                        }
                    } else {
                        Platform.runLater(() -> messageLabel.setText("❌ User not found in the system."));
                    }
                } else {
                    Platform.runLater(() -> messageLabel.setText("❌ Face not recognized."));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> messageLabel.setText("❌ Error during face recognition login."));
            }
        }).start();
    }


    //    private void startCountdown(long remainingSeconds, String email) {
//        lockoutMessage.setVisible(true);
//        loginButton.setDisable(true);
//
//        long[] remainingTime = {remainingSeconds}; // Wrap in an array to modify inside lambda
//
//        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//            if (remainingTime[0] > 0) {
//                long minutes = remainingTime[0] / 60;
//                long seconds = remainingTime[0] % 60;
//                lockoutMessage.setText("🔒 Account locked. Try again in: " + minutes + "m " + seconds + "s");
//                remainingTime[0]--;
//            } else {
//                lockoutMessage.setText("✅ You can now log in.");
//                loginButton.setDisable(false);
//                countdownTimer.stop(); // Stop when finished
//            }
//        }));
//
//        countdownTimer.setCycleCount((int) remainingSeconds);
//        countdownTimer.play();
//    }
    private void startLockoutTimer(long remainingSeconds) {
        loginButton.setDisable(true); // Disable the login button

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            long remainingTime = remainingSeconds;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (remainingTime > 0) {
                        long minutes = remainingTime / 60;
                        long seconds = remainingTime % 60;
                        messageLabel.setText("⛔ Trop de tentatives. Réessayer dans " + minutes + " min " + seconds + " sec.");
                        remainingTime--; // Decrease time
                    } else {
                        messageLabel.setText(""); // Clear the message
                        loginButton.setDisable(false); // Re-enable login button
                        timer.cancel(); // Stop the timer
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000); // Run every second
    }


    private String captureImage() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720);

        if (!camera.isOpened()) {
            System.out.println("❌ Error: Camera not found.");
            return null;
        }

        Mat frame = new Mat();
        if (!camera.read(frame)) {  // Ensure the frame is captured
            System.out.println("❌ Error: Failed to capture frame.");
            camera.release();
            return null;
        }

        String imagePath = "captured_face.jpg";
        Imgcodecs.imwrite(imagePath, frame);
        camera.release();
        return imagePath;
    }

    private String sendImageToRecognition(String imagePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "C:\\Python\\face_recognition_api.py", imagePath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // ✅ Only extract the email (ignore TensorFlow logs)
                if (line.contains("@")) {
                    output.append(line.trim()).append("\n");
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Python script output:\n" + output.toString());

            if (exitCode != 0 || output.toString().trim().isEmpty()) {
                System.err.println("❌ Face recognition failed. Exit code: " + exitCode);
                return null;
            }

            return output.toString().trim();  // Extract user email from output
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void goBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Page_acceuil.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

