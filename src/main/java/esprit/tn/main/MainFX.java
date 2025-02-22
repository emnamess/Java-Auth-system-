package esprit.tn.main;

import esprit.tn.entities.JwtUtils;
import esprit.tn.entities.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        String savedToken = loadTokenFromFile();

        if (savedToken != null && JwtUtils.extractRole(savedToken) != null) {
            // Auto-login: Load the correct dashboard
            String userType = JwtUtils.extractRole(savedToken);
            SessionManager.setToken(savedToken);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + userType + ".fxml"));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Dashboard");
            System.out.println("✅ Auto-login successful! Redirecting to dashboard...");
        } else {
            // No valid token: Show login/home page
            Parent root = FXMLLoader.load(getClass().getResource("/Page_acceuil.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Welcome");
            System.out.println("🔑 No valid token found. Showing home page.");
        }

        primaryStage.show();
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

    public static void main(String[] args) {
        launch(args);
    }
}
