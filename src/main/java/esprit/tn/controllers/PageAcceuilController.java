package esprit.tn.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class PageAcceuilController {

    @FXML
    private void handlePartenaireSelection(ActionEvent event) {
        navigateToAjouterUser(event, "partenaire");
    }

    @FXML
    private void handleParticipantSelection(ActionEvent event) {
        navigateToAjouterUser(event, "participant");
    }

    @FXML
    private void handleOrganisateurSelection(ActionEvent event) {
        navigateToAjouterUser(event, "organisateur");
    }

    private void navigateToAjouterUser(ActionEvent event, String userType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUser.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user instance type
            AjouterUserController controller = loader.getController();
            controller.setUserInstance(userType);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
