package esprit.tn.controllers;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import esprit.tn.entities.Billet;
import esprit.tn.entities.typeBillet;
import esprit.tn.services.BilletService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import esprit.tn.main.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ResourceBundle;

public class AjouterBilletController implements Initializable {

    @FXML
    private ChoiceBox<typeBillet> typeebillet;

    @FXML
    private TextArea descriptionbillet;

    @FXML
    private TextField nbrplaces;

    @FXML
    private TextField prixbillet;

    @FXML
    private TextField titrebillet;

    @FXML
    private ImageView qrCodeImage; // Pour afficher le QR Code généré

    private int idEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ajouter les valeurs de l'énumération à la ChoiceBox
        ObservableList<typeBillet> billetTypes = FXCollections.observableArrayList(typeBillet.values());
        typeebillet.setItems(billetTypes);

        // Optionnel : définir une valeur par défaut
        typeebillet.setValue(typeBillet.PARTICIPANT);
        // Vérification que l'ImageView est bien initialisé
        if (qrCodeImage != null) {
            System.out.println("qrCodeImage est bien initialisé.");
        } else {
            System.out.println("qrCodeImage n'est pas initialisé.");
        }
    }

    @FXML
    void ajouterbilletaction(ActionEvent event) {
        String titre = titrebillet.getText();
        String description = descriptionbillet.getText();
        String prixStr = prixbillet.getText();
        String nbrPlacesStr = nbrplaces.getText();
        typeBillet type = typeebillet.getValue();

        if (titre.isEmpty() || description.isEmpty() || prixStr.isEmpty() || nbrPlacesStr.isEmpty() || type == null) {
            showAlert("Erreur", "Tous les champs doivent être remplis.", Alert.AlertType.ERROR);
            return;
        }

        double prix;
        int nbrPlaces;
        try {
            prix = Double.parseDouble(prixStr);
            nbrPlaces = Integer.parseInt(nbrPlacesStr);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix et le nombre de places doivent être des nombres valides.", Alert.AlertType.ERROR);
            return;
        }

        Billet billet = new Billet(getIdEvent(), titre, description, prix, nbrPlaces, type,null);
        System.out.println("ID du billet : " + billet.getId_billet());
        showAlert("Succès", "Billet ajouté avec succès !", Alert.AlertType.INFORMATION);
        clearFields();

        // Générer le QR Code et récupérer le chemin du fichier
        String qrCodePath = genererQRCode(billet.getId_billet());

        if (qrCodePath != null) {
            // 🟢 Récupérer la connexion à la base de données
            Connection conn = DatabaseConnection.getInstance().getCnx();

            // 🟢 Mettre à jour le chemin du QR Code pour ce billet
            billet.setQrCodePath(qrCodePath);
            BilletService billetService = new BilletService();

            // Ajouter le billet
            billetService.ajouter(billet);
// Récupérer l'ID généré pour ce billet
            int idBillet = billetService.getDernierId(); // Exemple d'une méthode pour récupérer l'ID du dernier billet inséré
            billet.setId_billet(idBillet);

            // 🟢 Afficher le QR Code dans l'interface JavaFX
            File file = new File(qrCodePath);
            if (file.exists() && qrCodeImage != null) {
                qrCodeImage.setImage(new Image(file.toURI().toString()));
            } else {

                System.out.println("❌ QR Code non trouvé ou ImageView non initialisé.");
            }
        } else {
            System.out.println("❌ Erreur lors de la génération du QR Code.");
        }

        // Charger l'écran de liste des billets ou autre action après ajout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CarddBillet.fxml"));
        Parent root = null;
        try {
            root = loader.load();

            //controller call
            CarddBilletController controller = loader.getController();
            controller.setData(billet);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String genererQRCode(int idBillet) {
        try {
            // Définir le chemin du dossier où sauvegarder le QR Code
            String dossierQR = "qrcodes";
            File dossier = new File(dossierQR);

            // Vérifier si le dossier existe, sinon le créer
            if (!dossier.exists()) {
                dossier.mkdirs(); // Crée le dossier et tous ses sous-dossiers si nécessaire
            }

            // Définir le chemin du fichier QR Code
            String nomFichier = "billet_" + System.currentTimeMillis() + ".png"; // Utilisation de l'heure actuelle pour créer un nom unique
            Path cheminFichier = Paths.get(dossierQR, nomFichier);

            // Génération du QR Code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode("Données QR Code pour le billet " + idBillet,
                    BarcodeFormat.QR_CODE,
                    200, 200);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", cheminFichier);

            return cheminFichier.toString(); // Retourne le chemin du fichier généré

        } catch (WriterException | IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de la génération du QR Code.");
            return null;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        titrebillet.clear();
        descriptionbillet.clear();
        prixbillet.clear();
        nbrplaces.clear();
        typeebillet.getSelectionModel().clearSelection();
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvent = idEvenement;
    }

    public int getIdEvent() {
        return idEvent;
    }

    // Méthode pour retourner à la liste des événements
    public void Listedesevenements(ActionEvent actionEvent) {
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
}