package esprit.tn.entities;

import esprit.tn.services.authentificationService;
import esprit.tn.services.userService;
import io.jsonwebtoken.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Timer;
import java.util.TimerTask;

public class SessionTimer {
    private static Timer sessionTimer;
    private static boolean warningShown = false;
    private static final authentificationService authService = new authentificationService();
    private static final userService userServiceInstance = new userService();

    public static void startSessionChecker() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }
        System.out.println("🕒 Session checker started...");

        sessionTimer = new Timer(true);
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long remainingTime = SessionManager.getRemainingSessionTime();
                System.out.println("⏳ Checking session time... Remaining: " + remainingTime + " seconds");

                if (remainingTime > 0 && remainingTime <= 60 && !warningShown) {
                    warningShown = true;
                    System.out.println("🚨 Triggering session expiry warning...");
                    showSessionExpiryWarning();
                }
            }
        }, 0, 30000); // Check every 30 seconds
    }

    private static void showSessionExpiryWarning() {
        Platform.runLater(() -> {
            System.out.println("🚨 Showing session expiry warning...");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Session Expiring Soon");
            alert.setHeaderText("Your session will expire in 1 minute.");
            alert.setContentText("Do you want to extend your session?");

            ButtonType extendButton = new ButtonType("Extend");
            ButtonType logoutButton = new ButtonType("Logout");

            alert.getButtonTypes().setAll(extendButton, logoutButton);
            alert.showAndWait().ifPresent(response -> {
                if (response == extendButton) {
                    extendSession();
                } else {
                    logoutUser();
                }
            });
        });
    }

    private static void extendSession() {
        Integer userId = SessionManager.getUserIdFromToken();
        if (userId == null || userId == 0) {
            logoutUser();
            return;
        }

        user authenticatedUser = userServiceInstance.getOne(userId);
        if (authenticatedUser == null) {
            logoutUser();
            return;
        }

        String role = authService.getRoleFromInstance(authenticatedUser);
        String newToken = authService.generateJwtToken(authenticatedUser, role);

        if (newToken == null || newToken.isEmpty()) {
            logoutUser();
            return;
        }

        System.out.println("✅ Session extended!");
        SessionManager.setToken(newToken);
        warningShown = false;

        if (sessionTimer != null) {
            sessionTimer.cancel(); // Cancel old timer
        }

        startSessionChecker(); // Restart session timer
    }

    private static void logoutUser() {  // For calls without an event
        logoutUser(null);
    }

    private static void logoutUser(ActionEvent event) {  // For button clicks
        System.out.println("⚠ LOGOUT TRIGGERED! Checking why...");
        System.out.println("Token: " + SessionManager.getToken());
        System.out.println("Remaining Time: " + SessionManager.getRemainingSessionTime());

        SessionManager.clearToken();
        if (SessionTimer.sessionTimer != null) {
            SessionTimer.sessionTimer.cancel();
        }

        // Clear token
        SessionManager.clearToken();

        // Stop session timer
        if (SessionTimer.sessionTimer != null) {
            SessionTimer.sessionTimer.cancel();
            System.out.println("🛑 Session timer stopped.");
        }

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(SessionTimer.class.getResource("/Login.fxml"));
                Parent root = loader.load();

                Stage stage;
                if (event != null) {
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                } else {
                    // Fallback: Get the current window manually
                    stage = (Stage) Window.getWindows().stream()
                            .filter(Window::isShowing)
                            .findFirst()
                            .orElse(null);
                }

                if (stage != null) {
                    stage.setScene(new Scene(root));
                    stage.show();
                }
            } catch (IOException | java.io.IOException e) {
                System.out.println("❌ Error loading Login screen: " + e.getMessage());
                e.printStackTrace(); // Print the full stack trace for debugging
            }
        });

    }


}
