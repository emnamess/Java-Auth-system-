package esprit.tn.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import esprit.tn.entities.user;
import esprit.tn.entities.organisateur;
import esprit.tn.entities.partenaire;
import esprit.tn.entities.participant;
import esprit.tn.main.DatabaseConnection;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class authentificationService {
    private static final String SECRET_KEY = "p4/JkLtFcXR/Kp2MWRYflQhINQGwYra953et6lb07eE=";
    private static final String TABLE_ORGANISATEUR = "organisateur";
    private static final String TABLE_PARTENAIRE = "partenaire";
    private static final String TABLE_PARTICIPANT = "participants";

    Connection cnx;

    public authentificationService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public user login(String email, String password) throws Exception {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("mot_de_passe");
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    int id = rs.getInt("Id_user");

                    // DEBUG: Print user ID from database
                    System.out.println("🛠 Retrieved userId from DB: " + id);

                    if (id == 0) {
                        System.out.println("❌ ERROR: userId is 0, check database!");
                        throw new Exception("Database error: userId is 0");
                    }

                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    LocalDate dateNaissance = rs.getDate("date_naiss").toLocalDate();
                    String adresse = rs.getString("adresse");
                    int telephone = rs.getInt("telephone");
                    LocalDate dateInscription = rs.getDate("date_inscription").toLocalDate();

                    user userInstance = null;
                    String userType = "";

                    if (isOrganisateur(id)) {
                        userInstance = getOrganisateur(id, nom, prenom, email, storedHashedPassword, dateNaissance, adresse, telephone, dateInscription);
                        userType = "organisateur";
                    } else if (isPartenaire(id)) {
                        userInstance = getPartenaire(id, nom, prenom, email, storedHashedPassword, dateNaissance, adresse, telephone, dateInscription);
                        userType = "partenaire";
                    } else if (isParticipant(id)) {
                        userInstance = getParticipant(id, nom, prenom, email, storedHashedPassword, dateNaissance, adresse, telephone, dateInscription);
                        userType = "participant";
                    }

                    if (userInstance != null) {
                        System.out.println("User Type Identified: " + userType);
                        String token = generateJwtToken(userInstance, userType);
                        userInstance.setJwtToken(token);
                        System.out.println("User logged in: " + userInstance);
                        return userInstance;
                    }
                }
            }
        }
        throw new Exception("Invalid credentials");
    }

    private String generateJwtToken(user authenticatedUser, String role) {
        System.out.println("🛠 Generating token for user ID: " + authenticatedUser.getId_user());

        if (authenticatedUser.getId_user() == 0) {
            System.out.println("❌ ERROR: userId is 0 before token generation!");
        }

        return JWT.create()
                .withClaim("userId", authenticatedUser.getId_user())
                .withClaim("email", authenticatedUser.getEmail())
                .withClaim("role", role)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }


    private String getRoleFromInstance(user authenticatedUser) {
        if (authenticatedUser instanceof organisateur) {
            return "organisateur";
        } else if (authenticatedUser instanceof partenaire) {
            return "partenaire";
        } else if (authenticatedUser instanceof participant) {
            return "participant";
        } else {
            return "unknown"; // Handle unexpected cases
        }
    }

    private boolean isOrganisateur(int Id_user) throws Exception {
        return checkUserType(Id_user, TABLE_ORGANISATEUR);
    }

    private boolean isPartenaire(int Id_user) throws Exception {
        return checkUserType(Id_user, TABLE_PARTENAIRE);
    }

    private boolean isParticipant(int Id_user) throws Exception {
        return checkUserType(Id_user, TABLE_PARTICIPANT);
    }

    private boolean checkUserType(int Id_user, String tableName) throws Exception {
        String query = "SELECT 1 FROM " + tableName + " WHERE Id_user = ? LIMIT 1";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private organisateur getOrganisateur(int Id_user, String nom, String prenom, String email, String password, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription) throws Exception {
        String query = "SELECT workField, workEmail FROM organisateur WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new organisateur(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription, rs.getString("workField"), rs.getString("workEmail"));
            }
        }
        return null;
    }

    private partenaire getPartenaire(int Id_user, String nom, String prenom, String email, String password, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription) throws Exception {
        String query = "SELECT typeService, siteWeb, nbreContrats FROM partenaire WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new partenaire(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription, rs.getString("typeService"), rs.getString("siteWeb"), rs.getInt("nbreContrats"));
            }
        }
        return null;
    }

    private participant getParticipant(int Id_user, String nom, String prenom, String email, String password,
                                       LocalDate dateNaissance, String adresse, int telephone,
                                       LocalDate dateInscription) throws Exception {
        String query = "SELECT nombreParticipations FROM participants WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                participant p = new participant(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription, rs.getInt("nombreParticipations"));

                // ✅ Explicitly assign the user ID
                p.setId_user(Id_user);

                // DEBUG: Check if ID is correctly assigned
                System.out.println("✅ Assigned user ID in getParticipant: " + p.getId_user());

                return p;
            }
        }
        return null;
    }



    public int extractUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);
            return decodedJWT.getClaim("userId").asInt(); // Extract user ID
        } catch (Exception e) {
            System.err.println("Error extracting userId from token: " + e.getMessage());
            return -1;
        }
    }

}
