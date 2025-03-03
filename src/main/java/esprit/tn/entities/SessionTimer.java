package esprit.tn.entities;

import esprit.tn.services.authentificationService;
import esprit.tn.services.userService;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Timer;
import java.util.TimerTask;

public class SessionTimer {
    private static Timer sessionTimer;
    private static boolean warningShown = false;
    private static final authentificationService authService = new authentificationService();
    private static final userService userServiceInstance = new userService();

    public static void startSessionChecker() {
        if (sessionTimer != null) {
            sessionTimer.cancel(); // Cancel previous timer if running
        }

        sessionTimer = new Timer(true);
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long remainingTime = SessionManager.getRemainingSessionTime();
                System.out.println("⏳ Checking session... Remaining: " + remainingTime + " seconds");

                if (remainingTime <= 0) {
                    System.out.println("❌ Session expired! Logging out.");
                    Platform.runLater(SessionTimer::logoutUser);
                    return;
                }

                if (remainingTime > 0 && remainingTime <= 60 && !warningShown) {
                    warningShown = true;
                    showSessionExpiryWarning();
                }
            }
        }, 0, 30000); // Every 30 sec
    }

    private static void showSessionExpiryWarning() {
        Platform.runLater(() -> { // Run UI updates on JavaFX thread
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
                    extendSession(); // Extend session
                } else {
                    logoutUser(); // Logout user
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

    private static void logoutUser() {
        System.out.println("🚪 Logging out user...");
        SessionManager.clearToken();
        Platform.exit(); // Close the application
    }
}
