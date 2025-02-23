package esprit.tn.entities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;

public class SceneController {

    public void switchToNewPage(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the current scene
            Scene currentScene = ((Node) event.getSource()).getScene();

            // Store it before switching
            NavigationManager.pushScene(currentScene);

            // Set the new scene
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void goBack(ActionEvent event) {
        if (NavigationManager.hasPreviousScene()) {
            Scene previousScene = NavigationManager.popScene();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(previousScene);
            stage.show();
        } else {
            System.out.println("⚠ No previous page found!");
        }
    }

}
