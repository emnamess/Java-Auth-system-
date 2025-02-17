package esprit.tn.controllers;

import esprit.tn.entities.organisateur;
import esprit.tn.entities.partenaire;
import esprit.tn.entities.participant;
import esprit.tn.entities.user;
import esprit.tn.services.userService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController {

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

    private TableColumn<user, String> extraColumn1;
    private TableColumn<user, String> extraColumn2;
    private TableColumn<user, Integer> extraColumn3;

    private final userService userService = new userService();
    private ObservableList<user> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadUsers(null); // Load all users by default

        btnOrganisateurs.setOnAction(e -> loadUsers(organisateur.class));
        btnParticipants.setOnAction(e -> loadUsers(participant.class));
        btnPartenaires.setOnAction(e -> loadUsers(partenaire.class));
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
        usersTable.getColumns().removeAll(extraColumn1, extraColumn2, extraColumn3);

        List<user> allUsers = userService.getall();
        List<user> filteredUsers;

        if (userType == null) {
            filteredUsers = allUsers;
        } else {
            filteredUsers = allUsers.stream()
                    .filter(userType::isInstance)
                    .collect(Collectors.toList());

            if (userType.equals(organisateur.class)) {
                extraColumn1 = new TableColumn<>("Work Field");
                extraColumn1.setCellValueFactory(new PropertyValueFactory<>("workField"));

                extraColumn2 = new TableColumn<>("Work Email");
                extraColumn2.setCellValueFactory(new PropertyValueFactory<>("workEmail"));

                usersTable.getColumns().addAll(extraColumn1, extraColumn2);
            } else if (userType.equals(partenaire.class)) {
                extraColumn1 = new TableColumn<>("Service Type");
                extraColumn1.setCellValueFactory(new PropertyValueFactory<>("typeService"));

                extraColumn2 = new TableColumn<>("Website");
                extraColumn2.setCellValueFactory(new PropertyValueFactory<>("siteWeb"));

                extraColumn3 = new TableColumn<>("Contracts");
                extraColumn3.setCellValueFactory(new PropertyValueFactory<>("nbreContrats"));

                usersTable.getColumns().addAll(extraColumn1, extraColumn2, extraColumn3);
            } else if (userType.equals(participant.class)) {
                extraColumn1 = new TableColumn<>("Participations");
                extraColumn1.setCellValueFactory(new PropertyValueFactory<>("nombreParticipations"));

                usersTable.getColumns().add(extraColumn1);
            }
        }

        userList.setAll(filteredUsers);
        usersTable.setItems(userList);
    }
}
