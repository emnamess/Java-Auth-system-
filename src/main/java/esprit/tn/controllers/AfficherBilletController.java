package esprit.tn.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import esprit.tn.entities.Billet;
import esprit.tn.entities.typeBillet;
import esprit.tn.services.BilletService;
import javafx.scene.control.TableView;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherBilletController implements Initializable {

    @FXML
    private TableColumn<Billet, String> descriptionbillet;

    @FXML
    private TableView<Billet> tableBillets;

    @FXML
    private TableColumn<Billet, Integer> nbrplaces;

    @FXML
    private TableColumn<Billet, Double> prixbillet;

    @FXML
    private TableColumn<Billet, String> titrebillet;

    @FXML
    private TableColumn<Billet, String> typeebillet;

    private BilletService billetService = new BilletService();
    private int idEvent;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Affichage des billets...");
        System.out.println("Affichage des billets..."+getIdEvent());

        // Configuration des colonnes
        titrebillet.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionbillet.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixbillet.setCellValueFactory(new PropertyValueFactory<>("prix"));
        nbrplaces.setCellValueFactory(new PropertyValueFactory<>("nbr_places"));
        typeebillet.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Charger les billets depuis la base de données
        loadBillets();
    }

    @FXML
    void supprimerBillet(ActionEvent event) {

    }

    private void loadBillets() {
        BilletService billetService = new BilletService(); // Initialisation du service
        try {
            List<Billet> billets = billetService.getAll();
            System.out.println("Billets chargés : " + billets);
            tableBillets.getItems().setAll(billets);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement des billets.", Alert.AlertType.ERROR);
        }
        /*
       BilletService billetService = new BilletService(); // Initialisation du service
        try {
            List<Billet> billets = billetService.getBilletByIdEvent(getIdEvent());
            System.out.println("Billets chargés : " + billets);
            tableBillets.getItems().setAll(billets);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement des billets.", Alert.AlertType.ERROR);
        }*/
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void retourPageAction(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEvenement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Événements");
            stage.show();

        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout de l'événement.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void supprimerBillet(javafx.event.ActionEvent actionEvent) {
        Billet  selectedBillet = tableBillets.getSelectionModel().getSelectedItem();
        try {
            billetService.remove(selectedBillet);
            showAlert("Succès", "Événement supprimé avec succès !", Alert.AlertType.INFORMATION);
            loadBillets(); // Refresh the table after deletion
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la suppression de l'événement."+e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    public void modifierBillet(ActionEvent actionEvent) {
        Billet selectedBillet = tableBillets.getSelectionModel().getSelectedItem();
        if (selectedBillet == null) {
            showAlert("Erreur", "Veuillez sélectionner un billet à modifier.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierBillet.fxml"));
            Parent root = loader.load();
            UpdateBilletController controller = loader.getController();
            controller.setDescriptionbillet(selectedBillet.getDescription());
            controller.setIdBillet(selectedBillet.getId_billet());
            controller.setIdEvenement(selectedBillet.getIdEvenement());
            controller.setPrixbillet(Double.valueOf(selectedBillet.getPrix()));
            controller.setTitrebillet(selectedBillet.getTitre());
            controller.setNbrplaces(selectedBillet.getNbr_places());
            //   controller.setTypeebillet(selectedBillet.getType());
            controller.setTypeebillet(FXCollections.observableArrayList(selectedBillet.getType()));
            //  Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            //stage.setScene(new Scene(root));
            //stage.setTitle("Modifier un Billet");
            //stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la modification du billet.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void modifierBillet(javafx.event.ActionEvent actionEvent) {
        Billet selectedBillet = tableBillets.getSelectionModel().getSelectedItem();
        if (selectedBillet == null) {
            showAlert("Erreur", "Veuillez sélectionner un billet à modifier.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateBillet.fxml"));
            Parent root = loader.load();
            UpdateBilletController controller = loader.getController();
            controller.setDescriptionbillet(selectedBillet.getDescription());
            controller.setIdBillet(selectedBillet.getId_billet());
            controller.setIdEvenement(selectedBillet.getIdEvenement());
            controller.setPrixbillet(selectedBillet.getPrix());
            controller.setTitrebillet(selectedBillet.getTitre());
            controller.setNbrplaces(selectedBillet.getNbr_places());
            typeBillet type = selectedBillet.getType();
            tableBillets.getScene().setRoot(root);
            //  controller.setTypeebillet(type);

            // Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            //stage.setScene(new Scene(root));
            //stage.setTitle("Modifier un Billet");
            //stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la modification du billet.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }
}



