package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.userService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController {
    @FXML private MenuButton notificationButton;
    @FXML private MenuItem menuNoMessages;
    @FXML private VBox chatContainer;
    @FXML private Label chatUserLabel;
   @FXML private TextField chatInputField;
    @FXML private Button sendChatButton;
    @FXML private Button btnOrganisateurs;
    @FXML private Button btnParticipants;
    @FXML private Button btnPartenaires;
    @FXML private TableView<user> usersTable;
    @FXML private TableColumn<user, String> nameColumn;
    @FXML private TableColumn<user, String> prenomColumn;
    @FXML private TableColumn<user, String> emailColumn;
    @FXML private TableColumn<user, String> date_naiss;
    @FXML private TableColumn<user, String> adresseColumn;
    @FXML private TableColumn<user, String> telColumn;
    @FXML private TableColumn<user, String> date_inscription;
    @FXML private TableColumn<user, Void> actionColumn; // Delete button column
    @FXML private TextField searchField;

    @FXML
    private Button logoutButton;




    @FXML private TableColumn<user, String> extraColumn1;
    @FXML private TableColumn<user, String> extraColumn2;
    @FXML private TableColumn<user, Integer> extraColumn3;
    @FXML
    private HBox topBar;
    private final userService userService = new userService();
    private ObservableList<user> userList = FXCollections.observableArrayList();
    private int currentChatUserId;
    private String currentChatUserName;



    @FXML
    public void initialize() {
        setupTable();
        loadUsers(null); // Load all users by default

        btnOrganisateurs.setOnAction(e -> loadUsers(organisateur.class));
        btnParticipants.setOnAction(e -> loadUsers(participant.class));
        btnPartenaires.setOnAction(e -> loadUsers(partenaire.class));

        // Search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers(newValue);
        });
        logoutButton.setOnAction(event -> handleLogout());
    }
    private void filterUsers(String searchTerm) {
        if (searchTerm.isEmpty()) {
            usersTable.setItems(userList); // Reset table if search is empty
            return;
        }

        List<user> filteredUsers;

        try {
            int id = Integer.parseInt(searchTerm);
            user foundUser = userService.getOne(id);
            filteredUsers = (foundUser != null) ? List.of(foundUser) : List.of();
        } catch (NumberFormatException e) {
            // If input is not an ID, use getByName to fetch from the database
            filteredUsers = userService.getByName(searchTerm);
        }

        usersTable.setItems(FXCollections.observableArrayList(filteredUsers));
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        date_naiss.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        telColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        date_inscription.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));

        setupDeleteButtonColumn();
    }



    private void setupDeleteButtonColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteButton.setOnAction(event -> {
                    user selectedUser = getTableView().getItems().get(getIndex());
                    deleteUser(selectedUser);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(deleteButton);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void deleteUser(user selectedUser) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cet utilisateur ?",
                ButtonType.YES, ButtonType.NO);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText(null);
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                userService.supprimer(selectedUser.getId_user());
                userList.remove(selectedUser);
                usersTable.refresh();
            }
        });
    }

    private void loadUsers(Class<? extends user> userType) {
        List<user> allUsers = userService.getall(); // Fetch all users

        if (userType != null) {
            // Filter users by type
            allUsers = allUsers.stream()
                    .filter(userType::isInstance)
                    .collect(Collectors.toList());
        }

        userList.setAll(allUsers);
        usersTable.setItems(userList);
    }



    private void handleLogout() {
        System.out.println("🔒 Logging out...");

        // Clear the session
        SessionManager.setToken(null);
        deleteTokenFile();

        // Load login screen
        loadLoginScreen();
    }

    private void deleteTokenFile() {
        File file = new File("auth_token.txt");
        if (file.exists()) {
            file.delete();
            System.out.println("🗑 Token file deleted.");
        }
    }
    public void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Page_acceuil.fxml")); // Change to your login/home page
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            System.out.println("✅ Redirected to login page.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading the login screen.");
        }
    }
}
