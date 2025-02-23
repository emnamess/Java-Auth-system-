package esprit.tn.controllers;

import esprit.tn.entities.SessionManager;
import esprit.tn.entities.user;
import esprit.tn.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class ModifierProfil{
    private final userService userService = new userService();

    @FXML
    private TextField nomField, prenomField, emailField, adresseField, telephoneField;
    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        Integer userId = SessionManager.getUserIdFromToken();
        if (userId == null) {
            messageLabel.setText("⚠ Erreur : Utilisateur non connecté.");
            return;
        }

        user currentUser = userService.getOne(userId);
        if (currentUser == null) {
            messageLabel.setText("⚠ Erreur : Impossible de récupérer les informations de l'utilisateur.");
            return;
        }

        // Set initial values
        nomField.setText(currentUser.getNom());
        prenomField.setText(currentUser.getPrenom());
        emailField.setText(currentUser.getEmail());
        adresseField.setText(currentUser.getAdresse());
        telephoneField.setText(String.valueOf(currentUser.getTelephone()));
    }

    @FXML
    private void handleModifierProfile(ActionEvent event) {
        Integer userId = SessionManager.getUserIdFromToken();
        if (userId == null) {
            messageLabel.setText("⚠ Erreur : Utilisateur non connecté.");
            return;
        }

        user currentUser = userService.getOne(userId);
        if (currentUser == null) {
            messageLabel.setText("⚠ Erreur : Utilisateur introuvable.");
            return;
        }

        // Update user information
        currentUser.setNom(nomField.getText());
        currentUser.setPrenom(prenomField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setAdresse(adresseField.getText());
        currentUser.setTelephone(Integer.parseInt(telephoneField.getText()));

        try {
            userService.modifier(currentUser); // Since it returns void, just call it
            messageLabel.setText("✅ Profil mis à jour avec succès !");
        } catch (Exception e) {
            messageLabel.setText("❌ Échec de la mise à jour : " + e.getMessage());
        }
    }
}
