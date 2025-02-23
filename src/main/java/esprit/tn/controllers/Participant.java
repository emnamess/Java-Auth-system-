package esprit.tn.controllers;

import esprit.tn.entities.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.File;
import java.io.IOException;

public class Participant {

    @FXML
    private Button logoutButton; // Make sure this ID matches the one in your FXML file

    @FXML
    private void initialize() {
        logoutButton.setOnAction(event -> handleLogout());
    }

    private void handleLogout() {
        System.out.println("🔒 Logging out...");

        // Clear the session
        SessionManager.setToken(null);
        deleteTokenFile();

        // Load login screen
        loadLoginScreen();
    }

    private void deleteTokenFile() {
        File file = new File("auth_token.txt");
        if (file.exists()) {
            file.delete();
            System.out.println("🗑 Token file deleted.");
        }
    }

    public void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Page_acceuil.fxml")); // Change to your login/home page
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            System.out.println("✅ Redirected to login page.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading the login screen.");
        }
    }
    public void gotomodifyprofile(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modifier_Profil.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            System.out.println("✅ Redirected to modifier page.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading the login screen.");
        }
    }
}
