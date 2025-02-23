package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.sql.Date;

public class ModifierProfil {
    private final userService userService = new userService();

    @FXML private TextField nomField, prenomField, emailField, adresseField, telephoneField;
    @FXML private Label messageLabel;
    @FXML private DatePicker dobField;  // Added for Date of Birth

    // Additional fields based on user type
    @FXML private TextField workField, workEmailField; // For Organisateur
    @FXML private TextField typeServiceField, siteWebField, nbreContratsField; // For Partenaire
    @FXML private TextField nombreParticipationsField; // For Participant

    @FXML private VBox organisateurFields, partenaireFields, participantFields;

    private user currentUser;

    @FXML
    public void initialize() {
        Integer userId = SessionManager.getUserIdFromToken();
        if (userId == null) {
            messageLabel.setText("⚠ Erreur : Utilisateur non connecté.");
            return;
        }

        currentUser = userService.getOne(userId);
        if (currentUser == null) {
            messageLabel.setText("⚠ Erreur : Impossible de récupérer les informations de l'utilisateur.");
            return;
        }

        // Fill common user fields
        nomField.setText(currentUser.getNom());
        prenomField.setText(currentUser.getPrenom());
        emailField.setText(currentUser.getEmail());
        adresseField.setText(currentUser.getAdresse());
        telephoneField.setText(String.valueOf(currentUser.getTelephone()));

        if (currentUser.getDateNaissance() != null) {
            dobField.setValue(currentUser.getDateNaissance());
        }


        // Check user type and display relevant fields
        if (currentUser instanceof organisateur) {
            organisateurFields.setVisible(true);
            organisateurFields.setManaged(true);

            organisateur orga = (organisateur) currentUser;
            workField.setText(orga.getWorkField());
            workEmailField.setText(orga.getWorkEmail());

        } else if (currentUser instanceof partenaire) {
            partenaireFields.setVisible(true);
            partenaireFields.setManaged(true);

            partenaire part = (partenaire) currentUser;
            typeServiceField.setText(part.getTypeService());
            siteWebField.setText(part.getSiteWeb());
            nbreContratsField.setText(String.valueOf(part.getNbreContrats()));

        } else if (currentUser instanceof participant) {
            participantFields.setVisible(true);
            participantFields.setManaged(true);

            participant part = (participant) currentUser;
            nombreParticipationsField.setText(String.valueOf(part.getNombreParticipations()));
        }
    }

    @FXML
    private void handleModifierProfile(ActionEvent event) {
        Integer userId = SessionManager.getUserIdFromToken();
        if (userId == null) {
            messageLabel.setText("⚠ Erreur : Utilisateur non connecté.");
            return;
        }

        if (currentUser == null) {
            messageLabel.setText("⚠ Erreur : Utilisateur introuvable.");
            return;
        }

        // Update common fields
        currentUser.setNom(nomField.getText());
        currentUser.setPrenom(prenomField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setAdresse(adresseField.getText());
        currentUser.setTelephone(Integer.parseInt(telephoneField.getText()));

        LocalDate selectedDate = dobField.getValue();
        if (selectedDate != null) {
            currentUser.setDateNaissance(selectedDate);
        }


        // Update specific fields based on user type
        if (currentUser instanceof organisateur) {
            organisateur orga = (organisateur) currentUser;
            orga.setWorkField(workField.getText());
            orga.setWorkEmail(workEmailField.getText());

        } else if (currentUser instanceof partenaire) {
            partenaire part = (partenaire) currentUser;
            part.setTypeService(typeServiceField.getText());
            part.setSiteWeb(siteWebField.getText());
            part.setNbreContrats(Integer.parseInt(nbreContratsField.getText()));

        } else if (currentUser instanceof participant) {
            participant part = (participant) currentUser;
            part.setNombreParticipations(Integer.parseInt(nombreParticipationsField.getText()));
        }

        try {
            userService.modifier(currentUser);
            messageLabel.setText("✅ Profil mis à jour avec succès !");
        } catch (Exception e) {
            messageLabel.setText("❌ Échec de la mise à jour : " + e.getMessage());
        }
    }


    @FXML
    private void handleGoBack(ActionEvent event) {
        SceneController sceneController = new SceneController();

        sceneController.goBack(event);
    }


}
