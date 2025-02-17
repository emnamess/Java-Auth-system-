package esprit.tn.controllers;
import java.time.LocalDate;
import esprit.tn.entities.organisateur;
import esprit.tn.entities.partenaire;
import esprit.tn.entities.participant;
import esprit.tn.entities.user;
import esprit.tn.services.userService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class AjouterUserController {

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

    @FXML private CheckBox organisateurCheckBox;
    @FXML private CheckBox partenaireCheckBox;
    @FXML private CheckBox participantCheckBox;
    @FXML private Label roleErrorLabel;

    @FXML private Button submitButton;

    private final userService userService = new userService();

    @FXML
    private void initialize() {
        organisateurCheckBox.setOnAction(event -> handleCheckboxSelection(organisateurCheckBox));
        partenaireCheckBox.setOnAction(event -> handleCheckboxSelection(partenaireCheckBox));
        participantCheckBox.setOnAction(event -> handleCheckboxSelection(participantCheckBox));

        // Add listeners for real-time validation
        nomField.textProperty().addListener((obs, oldVal, newVal) -> validateNom());
        prenomField.textProperty().addListener((obs, oldVal, newVal) -> validatePrenom());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateEmail());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateConfirmPassword());
        telephoneField.textProperty().addListener((obs, oldVal, newVal) -> validatePhone());
        dobField.valueProperty().addListener((obs, oldVal, newVal) -> validateDOB());
        adresseField.textProperty().addListener((obs, oldVal, newVal) -> validateAddress());

        // Initially disable the button
        submitButton.setDisable(true);
    }

    private void handleCheckboxSelection(CheckBox selected) {
        organisateurCheckBox.setSelected(selected == organisateurCheckBox);
        partenaireCheckBox.setSelected(selected == partenaireCheckBox);
        participantCheckBox.setSelected(selected == participantCheckBox);
        validateRole();
    }

    private void validateNom() {
        if (nomField.getText().trim().isEmpty()) {
            nomErrorLabel.setText("Nom obligatoire !");
        } else {
            nomErrorLabel.setText("");
        }
        validateForm();
    }

    private void validatePrenom() {
        if (prenomField.getText().trim().isEmpty()) {
            prenomErrorLabel.setText("Prénom obligatoire !");
        } else {
            prenomErrorLabel.setText("");
        }
        validateForm();
    }

    private void validateEmail() {
        if (emailField.getText().isEmpty()) {
            emailErrorLabel.setText("Email obligatoire !");
        } else if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
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
        if (!confirmPasswordField.getText().equals(passwordField.getText())) {
            confirmPasswordErrorLabel.setText("Les mots de passe ne correspondent pas !");
        } else {
            confirmPasswordErrorLabel.setText("");
        }
        validateForm();
    }

    private void validatePhone() {
        if (!telephoneField.getText().matches("\\d{8}")) {
            telephoneErrorLabel.setText("Numéro invalide ! (8 chiffres)");
        } else {
            telephoneErrorLabel.setText("");
        }
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
        if (adresseField.getText().trim().isEmpty()) {
            adresseErrorLabel.setText("Adresse obligatoire !");
        } else {
            adresseErrorLabel.setText("");
        }
        validateForm();
    }

    private void validateRole() {
        if (!organisateurCheckBox.isSelected() && !partenaireCheckBox.isSelected() && !participantCheckBox.isSelected()) {
            roleErrorLabel.setText("Sélectionnez un rôle !");
        } else {
            roleErrorLabel.setText("");
        }
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
                adresseErrorLabel.getText().isEmpty() &&
                roleErrorLabel.getText().isEmpty();

        submitButton.setDisable(!isValid);
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        // Create user object based on the selected role
        LocalDate dateInscription = LocalDate.now();
        user newUser = null;

        if (participantCheckBox.isSelected()) {
            newUser = new participant(nomField.getText(), prenomField.getText(), emailField.getText(),
                    passwordField.getText(), dobField.getValue(), adresseField.getText(),
                    Integer.parseInt(telephoneField.getText()), dateInscription, 0);
        } else if (partenaireCheckBox.isSelected()) {
            newUser = new partenaire(nomField.getText(), prenomField.getText(), emailField.getText(),
                    passwordField.getText(), dobField.getValue(), adresseField.getText(),
                    Integer.parseInt(telephoneField.getText()), dateInscription, "TypeService", "SiteWeb", 0);
        } else if (organisateurCheckBox.isSelected()) {
            newUser = new organisateur(nomField.getText(), prenomField.getText(), emailField.getText(),
                    passwordField.getText(), dobField.getValue(), adresseField.getText(),
                    Integer.parseInt(telephoneField.getText()), dateInscription, "WorkField", "WorkEmail");
        }

        userService.ajouter(newUser);
        showAlert("Succès", "Utilisateur ajouté avec succès !");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
