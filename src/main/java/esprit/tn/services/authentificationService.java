package esprit.tn.services;

import esprit.tn.entities.user;
import esprit.tn.entities.organisateur;
import esprit.tn.entities.partenaire;
import esprit.tn.entities.participant;
import esprit.tn.main.DatabaseConnection;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class authentificationService {
    private static final String SECRET_KEY = "p4/JkLtFcXR/Kp2MWRYflQhINQGwYra953et6lb07eE=";

    Connection cnx;

    public authentificationService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public String login(String email, String password) throws Exception {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("mot_de_passe");
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    int id = rs.getInt("Id_user");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    LocalDate dateNaissance = rs.getDate("date_naiss").toLocalDate();
                    String adresse = rs.getString("adresse");
                    int telephone = rs.getInt("telephone");
                    LocalDate dateInscription = rs.getDate("date_inscription").toLocalDate();

                    // Determine user type
                    String userType = null;
                    user userInstance = null;

                    if (isOrganisateur(id)) {
                        userType = "organisateur";
                        userInstance = getOrganisateur(id, nom, prenom, email, storedHashedPassword, dateNaissance, adresse, telephone, dateInscription);
                    } else if (isPartenaire(id)) {
                        userType = "partenaire";
                        userInstance = getPartenaire(id, nom, prenom, email, storedHashedPassword, dateNaissance, adresse, telephone, dateInscription);
                    } else if (isParticipant(id)) {
                        userType = "participant";
                        userInstance = getParticipant(id, nom, prenom, email, storedHashedPassword, dateNaissance, adresse, telephone, dateInscription);
                    }

                    if (userInstance != null) {
                        return generateJwtToken(userInstance, userType);
                    }
                }
            }
        }
        throw new Exception("Invalid credentials");
    }

    private String generateJwtToken(user userInstance, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userInstance.getId_user());
        claims.put("nom", userInstance.getNom());
        claims.put("prenom", userInstance.getPrenom());
        claims.put("email", userInstance.getEmail());
        claims.put("userType", userType);

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(3600); // Token valid for 1 hour

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
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
