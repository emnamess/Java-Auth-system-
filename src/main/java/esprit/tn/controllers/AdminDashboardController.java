package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ChatService;
import esprit.tn.services.userService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    @FXML private TableView<String> chatMessagesTable;
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
    private TableColumn<user, String> extraColumn1;
    private TableColumn<user, String> extraColumn2;
    private TableColumn<user, Integer> extraColumn3;

    private final userService userService = new userService();
    private ObservableList<user> userList = FXCollections.observableArrayList();
    private final ChatService chatService = new ChatService();
    private int adminId = 20;
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
    private void loadNotifications() {
        List<Chat> unreadMessages = chatService.getUnreadMessages(adminId);

        notificationButton.getItems().clear();
        if (unreadMessages.isEmpty()) {
            notificationButton.getItems().add(menuNoMessages);
        } else {
            for (Chat chat : unreadMessages) {
                MenuItem chatItem = new MenuItem("💬 " + chat.getSenderId());
                chatItem.setOnAction(e -> openChat(chat.getSenderId(), chat.getSenderId()));
                notificationButton.getItems().add(chatItem);
            }
        }
    }
    private void openChat(int userId, String userName) {
        chatContainer.setVisible(true);
        chatUserLabel.setText("Chat avec " + userName);

        List<Chat> messages = chatService.getMessages(userId, adminId);
        chatMessagesTable.getItems().clear();
        for (Chat msg : messages) {
            chatMessagesTable.getItems().add(msg.getMessageText());
        }

        sendChatButton.setOnAction(e -> sendMessage(userId));
    }
    private void sendMessage(int receiverId) {
        String messageText = chatInputField.getText();
        if (!messageText.isEmpty()) {
            Chat newMessage = new Chat(adminId, receiverId, messageText);
            chatService.sendMessage(newMessage);
            chatInputField.clear();
            openChat(receiverId, chatUserLabel.getText().replace("Chat avec ", ""));
        }
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
        List<user> filteredUsers = userService.getall();

        if (userType != null) {
            filteredUsers = filteredUsers.stream()
                    .filter(userType::isInstance)
                    .collect(Collectors.toList());
        }

        userList.setAll(filteredUsers);
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
