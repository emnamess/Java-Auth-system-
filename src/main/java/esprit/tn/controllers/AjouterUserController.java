package esprit.tn.controllers;

import java.time.LocalDate;
import esprit.tn.entities.organisateur;
import esprit.tn.entities.partenaire;
import esprit.tn.entities.participant;
import esprit.tn.entities.user;
import esprit.tn.services.userService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class AjouterUserController {
    @FXML private TextField workField;
    @FXML private TextField workEmailField;

    @FXML private TextField typeServiceField;
    @FXML private TextField siteWebField;
    @FXML private TextField nbreContratsField;

    @FXML private TextField nombreParticipationsField;

    @FXML private TextField nomField;
    @FXML private Label nomErrorLabel;
    @FXML private TextField prenomField;
    @FXML private Label prenomErrorLabel;
    @FXML private TextField emailField;
    @FXML private Label emailErrorLabel;
    @FXML private PasswordField passwordField;
    @FXML private Label passwordErrorLabel;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private DatePicker dobField;
    @FXML private Label dobErrorLabel;
    @FXML private TextField adresseField;
    @FXML private Label adresseErrorLabel;
    @FXML private TextField telephoneField;
    @FXML private Label telephoneErrorLabel;
    @FXML private Button submitButton;

    @FXML private VBox organisateurFields;
    @FXML private VBox partenaireFields;
    @FXML private VBox participantFields;

    private final userService userService = new userService();
    private String userType; // Variable to store the user type

    public void setUserInstance(String userType) {
        this.userType = userType;
        toggleFields(); // Show relevant fields based on the instance
    }

    @FXML
    private void initialize() {
        nomField.textProperty().addListener((obs, oldVal, newVal) -> validateNom());
        prenomField.textProperty().addListener((obs, oldVal, newVal) -> validatePrenom());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateEmail());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateConfirmPassword());
        telephoneField.textProperty().addListener((obs, oldVal, newVal) -> validatePhone());
        dobField.valueProperty().addListener((obs, oldVal, newVal) -> validateDOB());
        adresseField.textProperty().addListener((obs, oldVal, newVal) -> validateAddress());

        submitButton.setDisable(true);
    }

    private void toggleFields() {
        boolean isOrganisateur = "organisateur".equals(userType);
        boolean isPartenaire = "partenaire".equals(userType);
        boolean isParticipant = "participant".equals(userType);

        organisateurFields.setVisible(isOrganisateur);
        organisateurFields.setManaged(isOrganisateur);
        workField.setDisable(!isOrganisateur);
        workEmailField.setDisable(!isOrganisateur);

        partenaireFields.setVisible(isPartenaire);
        partenaireFields.setManaged(isPartenaire);
        typeServiceField.setDisable(!isPartenaire);
        siteWebField.setDisable(!isPartenaire);
        nbreContratsField.setDisable(!isPartenaire);

        participantFields.setVisible(isParticipant);
        participantFields.setManaged(isParticipant);
        nombreParticipationsField.setDisable(!isParticipant);
    }

    private void validateNom() {
        nomErrorLabel.setText(nomField.getText().trim().isEmpty() ? "Nom obligatoire !" : "");
        validateForm();
    }

    private void validatePrenom() {
        prenomErrorLabel.setText(prenomField.getText().trim().isEmpty() ? "Prénom obligatoire !" : "");
        validateForm();
    }

    private void validateEmail() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            emailErrorLabel.setText("Email obligatoire !");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            emailErrorLabel.setText("Email invalide !");
        } else {
            emailErrorLabel.setText("");
        }
        validateForm();
    }

    private void validatePassword() {
        String password = passwordField.getText();
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Mot de passe obligatoire !");
        } else if (password.length() < 8 || !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            passwordErrorLabel.setText("Min 8 caractères, 1 majuscule, 1 minuscule, 1 chiffre !");
        } else {
            passwordErrorLabel.setText("");
        }
        validateForm();
    }

    private void validateConfirmPassword() {
        confirmPasswordErrorLabel.setText(
                !confirmPasswordField.getText().equals(passwordField.getText()) ? "Les mots de passe ne correspondent pas !" : ""
        );
        validateForm();
    }

    private void validatePhone() {
        telephoneErrorLabel.setText(!telephoneField.getText().matches("\\d{8}") ? "Numéro invalide ! (8 chiffres)" : "");
        validateForm();
    }

    private void validateDOB() {
        LocalDate dob = dobField.getValue();
        if (dob == null) {
            dobErrorLabel.setText("Veuillez choisir une date !");
        } else if (dob.isAfter(LocalDate.now().minusYears(18))) {
            dobErrorLabel.setText("Vous devez avoir au moins 18 ans !");
        } else {
            dobErrorLabel.setText("");
        }
        validateForm();
    }

    private void validateAddress() {
        adresseErrorLabel.setText(adresseField.getText().trim().isEmpty() ? "Adresse obligatoire !" : "");
        validateForm();
    }

    private void validateForm() {
        boolean isValid = nomErrorLabel.getText().isEmpty() &&
                prenomErrorLabel.getText().isEmpty() &&
                emailErrorLabel.getText().isEmpty() &&
                passwordErrorLabel.getText().isEmpty() &&
                confirmPasswordErrorLabel.getText().isEmpty() &&
                telephoneErrorLabel.getText().isEmpty() &&
                dobErrorLabel.getText().isEmpty() &&
                adresseErrorLabel.getText().isEmpty();

        submitButton.setDisable(!isValid);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        LocalDate dateInscription = LocalDate.now();
        user newUser = null;

        if ("participant".equals(userType)) {
            newUser = new participant(nomField.getText(), prenomField.getText(), emailField.getText(),
                    passwordField.getText(), dobField.getValue(), adresseField.getText(),
                    Integer.parseInt(telephoneField.getText()), dateInscription,
                    Integer.parseInt(nombreParticipationsField.getText()));
        } else if ("partenaire".equals(userType)) {
            newUser = new partenaire(nomField.getText(), prenomField.getText(), emailField.getText(),
                    passwordField.getText(), dobField.getValue(), adresseField.getText(),
                    Integer.parseInt(telephoneField.getText()), dateInscription,
                    typeServiceField.getText(), siteWebField.getText(),
                    Integer.parseInt(nbreContratsField.getText()));
        } else if ("organisateur".equals(userType)) {
            newUser = new organisateur(nomField.getText(), prenomField.getText(), emailField.getText(),
                    passwordField.getText(), dobField.getValue(), adresseField.getText(),
                    Integer.parseInt(telephoneField.getText()), dateInscription,
                    workField.getText(), workEmailField.getText());
        }

        if (newUser != null) {
            userService.ajouter(newUser);
            showAlert("Succès", "Utilisateur ajouté avec succès !");
        }
    }
}
