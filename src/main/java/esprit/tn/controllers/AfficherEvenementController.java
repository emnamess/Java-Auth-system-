package esprit.tn.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import esprit.tn.entities.Evenement;
import esprit.tn.services.EvenementService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherEvenementController implements Initializable {

    @FXML
    private Button btnajouterevenement;

    @FXML
    private Button btnsupprimerevenement;

    @FXML
    private Button btnupdateevenement;

    @FXML
    private Button btnajouterbillet;

    @FXML
    private TableColumn<Evenement, Integer> capaciteevenement;

    @FXML
    private TableColumn<Evenement, String> dateevenement;

    @FXML
    private TableColumn<Evenement, String> descriptionevenement;

    @FXML
    private TableColumn<Evenement, String> heureevenement;

    @FXML
    private TableColumn<Evenement, Integer> id_evenement;

    @FXML
    private TableColumn<Evenement, String> lieuevenement;

    @FXML
    private TableColumn<Evenement, String> statutevenement;

    @FXML
    private TableColumn<Evenement, String> titreevenement;

    @FXML
    private TableColumn<Evenement, String> typeevenement;

    @FXML
    private TableColumn<Evenement, String> imageUrlevenement;

    @FXML
    private TableView<Evenement> tableEvenements;

    private EvenementService evenementService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        evenementService = new EvenementService();
        loadEvenements();

        // Set up table columns to map to Evenement attributes
        titreevenement.setCellValueFactory(new PropertyValueFactory<>("titre"));
        dateevenement.setCellValueFactory(new PropertyValueFactory<>("date"));
        heureevenement.setCellValueFactory(new PropertyValueFactory<>("heure"));
        lieuevenement.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        statutevenement.setCellValueFactory(new PropertyValueFactory<>("stat"));
        capaciteevenement.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        typeevenement.setCellValueFactory(new PropertyValueFactory<>("typee"));
        imageUrlevenement.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));
    }

    public void loadEvenements() {
        try {
            List<Evenement> evenements = evenementService.getAllEvenementsByLieu();
            tableEvenements.getItems().setAll(evenements);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement des événements.", AlertType.ERROR);
        }
    }

    @FXML
    void ajouterevenement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEvenement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Événements");
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout de l'événement.", AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerevenement(ActionEvent event) {
        Evenement selectedEvenement = tableEvenements.getSelectionModel().getSelectedItem();
        try {
            evenementService.remove(selectedEvenement);
            showAlert("Succès", "Événement supprimé avec succès !", AlertType.INFORMATION);
            loadEvenements(); // Refresh the table after deletion
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la suppression de l'événement.", AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void updateevenement(ActionEvent event) {
        Evenement selectedEvenement = tableEvenements.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierEvent.fxml"));
            Parent root = loader.load();
            ModifierEventController controller = loader.getController();
            String capacite = Integer.toString(selectedEvenement.getCapacite());
            controller.setTxtcapaciteevenement(capacite);
            controller.setTxtheureevenement(selectedEvenement.getHeure().toString());
            controller.setTxttitreevenement(selectedEvenement.getTitre());
            controller.setTxtlieuevenement(selectedEvenement.getLieu().getNom());
            controller.setTxtdescriptionevenement(selectedEvenement.getDescription());
            controller.setTxtdateevenement(selectedEvenement.getDate());
            controller.setIdLieu(selectedEvenement.getLieu().getIdLieu());
            controller.setTxtstatutevenement(selectedEvenement.getStat());
            //      controller.setTypee(FXCollections.observableArrayList(typeEvenement.values())); // Set the typee field with enum values
            controller.setTxtimageEvenement(selectedEvenement.getImageUrl());
            controller.setIdEvent(selectedEvenement.getIdEvenement());

            tableEvenements.getScene().setRoot(root);
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour de l'événement.", AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void ajouterbilletaction(ActionEvent event) {
        try {
            Evenement selectedEvenement = tableEvenements.getSelectionModel().getSelectedItem();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterBillet.fxml"));
            Parent root = loader.load();
            AjouterBilletController controller = loader.getController();
            controller.setIdEvenement(selectedEvenement.getIdEvenement());
            tableEvenements.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la page.", AlertType.ERROR);
        }
    }
}