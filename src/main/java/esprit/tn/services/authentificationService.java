package esprit.tn.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import esprit.tn.entities.*;
import esprit.tn.main.DatabaseConnection;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.IOException;
import org.mindrot.jbcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class authentificationService {
    private static final String SECRET_KEY = "p4/JkLtFcXR/Kp2MWRYflQhINQGwYra953et6lb07eE=";
    private static final String TABLE_ORGANISATEUR = "organisateur";
    private static final String TABLE_PARTENAIRE = "partenaire";
    private static final String TABLE_PARTICIPANT = "participants";
    private static final String TABLE_ADMIN = "admin";


    Connection cnx;

    public authentificationService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Returns false if no user is found
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
                    }else if (isAdmin(id)) {
                        userInstance = getAdmin(id, nom, prenom, email, storedHashedPassword, dateNaissance, adresse, telephone, dateInscription);
                        userType = "admin";
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
    private boolean isAdmin(int Id_user) throws Exception {
        return checkUserType(Id_user, TABLE_ADMIN);
    }

    private boolean checkUserType(int Id_user, String tableName) throws Exception {
        String query = "SELECT 1 FROM " + tableName + " WHERE Id_user = ? LIMIT 1";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private organisateur getOrganisateur(int Id_user, String nom, String prenom, String email, String password,
                                         LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription) throws Exception {
        String query = "SELECT workField, workEmail FROM organisateur WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                organisateur o = new organisateur(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription,
                        rs.getString("workField"), rs.getString("workEmail"));
                o.setId_user(Id_user); // ✅ Ensure the user ID is explicitly set
                System.out.println("✅ Assigned user ID in getOrganisateur: " + o.getId_user()); // Debugging
                return o;
            }
        }
        return null;
    }

    private partenaire getPartenaire(int Id_user, String nom, String prenom, String email, String password,
                                     LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription) throws Exception {
        String query = "SELECT type_Service, site_Web, nbre_contacts FROM partenaire WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                partenaire p = new partenaire(nom, prenom, email, password, dateNaissance, adresse, telephone, dateInscription,
                        rs.getString("type_service"), rs.getString("site_web"), rs.getInt("nbre_contacts"));
                p.setId_user(Id_user); // ✅ Ensure the user ID is explicitly set
                System.out.println("✅ Assigned user ID in getPartenaire: " + p.getId_user()); // Debugging
                return p;
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
    private admin getAdmin(int Id_user, String nom, String prenom, String email, String password,
                           LocalDate dateNaissance, String adresse, int telephone,
                           LocalDate dateInscription) throws Exception {

        String query = "SELECT createdAt FROM admin WHERE Id_user = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Id_user);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Convert SQL Date to LocalDate
                LocalDate createdAt = rs.getDate("createdAt").toLocalDate();

                // ✅ Create an admin instance
                admin adminUser = new admin(nom, prenom, email, password, dateNaissance, adresse,
                        telephone, dateInscription, createdAt);

                // ✅ Explicitly assign the user ID
                adminUser.setId_user(Id_user);

                // DEBUG: Verify the ID assignment
                System.out.println("✅ Assigned user ID in getAdmin: " + adminUser.getId_user());

                return adminUser;
            }
        }

        return null; // No admin found
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
    public void storeVerificationCode(String email, String verificationCode) {
        String query = "UPDATE user SET verification_code = ?, verification_expiry = DATE_ADD(NOW(), INTERVAL 10 MINUTE) WHERE email = ?";

        try (
             PreparedStatement statement = cnx.prepareStatement(query)) {

            statement.setString(1, verificationCode);
            statement.setString(2, email);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("❌ No user found with this email.");
            } else {
                System.out.println("✅ Verification code stored successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean verifyCode(String email, String enteredCode) {
        String query = "SELECT verification_code, verification_expiry FROM user WHERE email = ?";
        try (
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedCode = rs.getString("verification_code");
                LocalDateTime expiry = rs.getTimestamp("verification_expiry").toLocalDateTime();

                if (LocalDateTime.now().isBefore(expiry) && storedCode.equals(enteredCode)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getStoredVerificationCode(String email) {
        String query = "SELECT verification_code FROM user WHERE email = ? AND verification_expiry > NOW()";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("verification_code");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(String email, String newPassword) {
        System.out.println("[DEBUG] Updating password for: " + email);

        try {
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt()); // Hash the new password
            String query = "UPDATE user SET mot_de_passe = ? WHERE email = ?";

            PreparedStatement pstmt = cnx.prepareStatement(query);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("[DEBUG] Rows updated: " + rowsUpdated);

            return rowsUpdated > 0; // Returns true if at least one row is updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public void saveImageToDatabase(String imagePath, int userId) {
        String query = "INSERT INTO faces (user_id, image_path) VALUES (?, ?)";

        try (
             PreparedStatement pstmt = cnx.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, imagePath);
            pstmt.executeUpdate();

            System.out.println("✅ Image path saved to database successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Error saving image to database: " + e.getMessage());
        }
    }

    public user loginWithFace(int userId) throws Exception {
        String query = "SELECT * FROM user WHERE Id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                LocalDate dateNaissance = rs.getDate("date_naiss").toLocalDate();
                String adresse = rs.getString("adresse");
                int telephone = rs.getInt("telephone");
                LocalDate dateInscription = rs.getDate("date_inscription").toLocalDate();

                user userInstance = null;
                String userType = "";

                if (isOrganisateur(userId)) {
                    userInstance = getOrganisateur(userId, nom, prenom, email, "", dateNaissance, adresse, telephone, dateInscription);
                    userType = "organisateur";
                } else if (isPartenaire(userId)) {
                    userInstance = getPartenaire(userId, nom, prenom, email, "", dateNaissance, adresse, telephone, dateInscription);
                    userType = "partenaire";
                } else if (isParticipant(userId)) {
                    userInstance = getParticipant(userId, nom, prenom, email, "", dateNaissance, adresse, telephone, dateInscription);
                    userType = "participant";
                } else if (isAdmin(userId)) {
                    userInstance = getAdmin(userId, nom, prenom, email, "", dateNaissance, adresse, telephone, dateInscription);
                    userType = "admin";
                }

                if (userInstance != null) {
                    System.out.println("✅ User Type Identified: " + userType);
                    String token = generateJwtToken(userInstance, userType); // Generate JWT
                    userInstance.setJwtToken(token);
                    System.out.println("✅ Face login successful for user: " + userInstance);
                    return userInstance;
                }
            }
        }
        throw new Exception("User not found or face not recognized");
    }

}
