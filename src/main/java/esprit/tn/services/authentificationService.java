package esprit.tn.services;

import esprit.tn.entities.user;
import esprit.tn.entities.organisateur;
import esprit.tn.entities.partenaire;
import esprit.tn.entities.participant;
import esprit.tn.main.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class authentificationService {

    Connection cnx;

    public authentificationService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public user login(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("mot_de_passe"); // Hashed password from DB
                System.out.println("Stored Hash from DB: " + storedHashedPassword);
                System.out.println("Stored Hash Length: " + storedHashedPassword.length());
                System.out.println("Attempting to verify password: " + password);

                if (BCrypt.checkpw(password, storedHashedPassword)) { // Compare hashed passwords
                    int id = rs.getInt("Id_user");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String mot_de_passe = storedHashedPassword; // Use stored hashed password
                    LocalDate dateNaissance = rs.getDate("date_naiss").toLocalDate();
                    String adresse = rs.getString("adresse");
                    int telephone = rs.getInt("telephone");
                    LocalDate dateInscription = rs.getDate("date_inscription").toLocalDate();

                    // 🔥 Check which table contains this user
                    if (isOrganisateur(id)) {
                        return getOrganisateur(id, nom, prenom, email, mot_de_passe, dateNaissance, adresse, telephone, dateInscription);
                    } else if (isPartenaire(id)) {
                        return getPartenaire(id, nom, prenom, email, mot_de_passe, dateNaissance, adresse, telephone, dateInscription);
                    } else if (isParticipant(id)) {
                        return getParticipant(id, nom, prenom, email, mot_de_passe, dateNaissance, adresse, telephone, dateInscription);
                    } else {
                        return new user(nom, prenom, email, mot_de_passe, dateNaissance, adresse, telephone, dateInscription); // Default user type
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Authentication failed
    }

    // 🟢 Check if user is an Organisateur
    private boolean isOrganisateur(int Id_user) throws Exception {
        return checkUserType(Id_user, "organisateur");
    }

    // 🟢 Check if user is a Partenaire
    private boolean isPartenaire(int Id_user) throws Exception {
        return checkUserType(Id_user, "partenaire");
    }

    // 🟢 Check if user is a Participant
    private boolean isParticipant(int Id_user) throws Exception {
        return checkUserType(Id_user, "participant");
    }

    private boolean checkUserType(int Id_user, String tableName) throws Exception {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // 🟢 Get Organisateur Data
    private organisateur getOrganisateur(int Id_user, String nom, String prenom, String email, String password, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription) throws Exception {
        String query = "SELECT workField, workEmail FROM organisateur WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String workField = rs.getString("workField");
                String workEmail = rs.getString("workEmail");
                return new organisateur(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription, workField, workEmail);
            }
        }
        return null;
    }

    // 🟢 Get Partenaire Data
    private partenaire getPartenaire(int Id_user, String nom, String prenom, String email, String password, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription) throws Exception {
        String query = "SELECT typeService, siteWeb, nbreContrats FROM partenaire WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String typeService = rs.getString("typeService");
                String siteWeb = rs.getString("siteWeb");
                int nbreContrats = rs.getInt("nbreContrats");
                return new partenaire(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription, typeService, siteWeb, nbreContrats);
            }
        }
        return null;
    }

    // 🟢 Get Participant Data
    private participant getParticipant(int Id_user, String nom, String prenom, String email, String password, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription) throws Exception {
        String query = "SELECT nombreParticipations FROM participant WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int nombreParticipations = rs.getInt("nombreParticipations");
                return new participant(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription, nombreParticipations);
            }
        }
        return null;
    }
}
