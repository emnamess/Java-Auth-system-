package esprit.tn.controllers;

import esprit.tn.entities.NavigationManager;
import esprit.tn.entities.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
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
    public void gotomodifyprofile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modifier_Profil.fxml"));
            Parent root = loader.load();

            // ✅ Get the current scene
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene currentScene = logoutButton.getScene();

            // ✅ Store the current scene before switching
            NavigationManager.pushScene(currentScene);

            // ✅ Switch to the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            System.out.println("✅ Redirected to modifier page.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading the modifier profile page.");
        }
    }

    @FXML
    private void goToFaceRecognition(ActionEvent event) {
        // Get the source of the event
        Object source = event.getSource();
        Stage stage = null;

        if (source instanceof MenuItem) {
            // If triggered by a MenuItem, get its parent stage
            stage = (Stage) ((MenuItem) source).getParentPopup().getOwnerWindow();
        } else if (source instanceof Node) {
            // If triggered by a button or other node, get the stage from the node
            stage = (Stage) ((Node) source).getScene().getWindow();
        }

        if (stage != null) {
            // Store the current scene before switching
            Scene currentScene = stage.getScene();
            NavigationManager.pushScene(currentScene);
            System.out.println("✅ Stored the previous scene. Stack size: " + NavigationManager.getStackSize());

            try {
                Parent root = FXMLLoader.load(getClass().getResource("/FaceRecognition.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                System.out.println("✅ Redirected to Face Recognition page.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("❌ Error loading the Face Recognition page.");
            }
        } else {
            System.out.println("❌ Unable to determine the stage.");
        }
    }


    public void gotomodifypassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();

            // ✅ Get the current scene
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene currentScene = logoutButton.getScene();

            // ✅ Store the current scene before switching
            NavigationManager.pushScene(currentScene);

            // ✅ Switch to the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            System.out.println("✅ Redirected to modify password page.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading the modify password page.");
        }
    }

}
