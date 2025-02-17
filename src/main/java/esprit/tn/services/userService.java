package esprit.tn.services;

import esprit.tn.entities.*;
import esprit.tn.main.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class userService implements Iservice<user> {
    Connection cnx;

    public userService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }



    public void ajouter(user user) {
        String reqUser = "INSERT INTO user (nom, prenom, email, mot_de_passe, date_naiss, adresse, telephone, date_inscription) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmUser = cnx.prepareStatement(reqUser, Statement.RETURN_GENERATED_KEYS);

            // Hash the password before inserting it
            String hashedPassword = BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt());

            stmUser.setString(1, user.getNom());
            stmUser.setString(2, user.getPrenom());
            stmUser.setString(3, user.getEmail());
            stmUser.setString(4, hashedPassword);  // ✅ Save hashed password instead of plain text
            stmUser.setDate(5, java.sql.Date.valueOf(user.getDateNaissance()));
            stmUser.setString(6, user.getAdresse());
            stmUser.setInt(7, user.getTelephone());
            stmUser.setDate(8, java.sql.Date.valueOf(user.getDateInscription()));

            stmUser.executeUpdate();

            ResultSet generatedKeys = stmUser.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1); // Get the generated id_user

                // If user is a participant, insert into participants table
                if (user instanceof participant) {
                    String reqParticipant = "INSERT INTO participants (id_user, nombreParticipations) VALUES (?, ?)";
                    PreparedStatement stmParticipant = cnx.prepareStatement(reqParticipant);
                    stmParticipant.setInt(1, userId);
                    stmParticipant.setInt(2, ((participant) user).getNombreParticipations());
                    stmParticipant.executeUpdate();
                }

                // If user is a partenaire, insert into partenaire table
                if (user instanceof partenaire) {
                    String reqPartenaire = "INSERT INTO partenaire (id_user, type_service, site_web, nbre_contacts) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmPartenaire = cnx.prepareStatement(reqPartenaire);
                    stmPartenaire.setInt(1, userId);
                    stmPartenaire.setString(2, ((partenaire) user).getTypeService());
                    stmPartenaire.setString(3, ((partenaire) user).getSiteWeb());
                    stmPartenaire.setInt(4, ((partenaire) user).getNbreContrats());
                    stmPartenaire.executeUpdate();
                }

                // If user is an organisateur, insert into organisateur table
                if (user instanceof organisateur) {
                    String reqOrganisateur = "INSERT INTO organisateur (id_user, workField, workEmail) VALUES (?, ?, ?)";
                    PreparedStatement stmOrganisateur = cnx.prepareStatement(reqOrganisateur);
                    stmOrganisateur.setInt(1, userId);
                    stmOrganisateur.setString(2, ((organisateur) user).getWorkField());
                    stmOrganisateur.setString(3, ((organisateur) user).getWorkEmail());
                    stmOrganisateur.executeUpdate();
                }

                // If user is an admin, insert into admin table
                if (user instanceof admin) {
                    String reqAdmin = "INSERT INTO admin (id_user, createdAt) VALUES (?, ?)";
                    PreparedStatement stmAdmin = cnx.prepareStatement(reqAdmin);
                    stmAdmin.setInt(1, userId);
                    stmAdmin.setDate(2, java.sql.Date.valueOf(((admin) user).getCreatedAt()));
                    stmAdmin.executeUpdate();
                }
            }

            System.out.println("Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void modifier(user user) {
        String reqUser = "UPDATE user SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, date_naiss = ?, adresse = ?, telephone = ?, date_inscription = ? WHERE id_user = ?";

        try {
            PreparedStatement stmUser = cnx.prepareStatement(reqUser);
            stmUser.setString(1, user.getNom());
            stmUser.setString(2, user.getPrenom());
            stmUser.setString(3, user.getEmail());
            stmUser.setString(4, user.getMotDePasse());
            stmUser.setDate(5, java.sql.Date.valueOf(user.getDateNaissance()));
            stmUser.setString(6, user.getAdresse());
            stmUser.setInt(7, user.getTelephone());
            stmUser.setDate(8, java.sql.Date.valueOf(user.getDateInscription()));
            stmUser.setInt(9, user.getId_user());

            int rowsAffected = stmUser.executeUpdate();

            if (rowsAffected > 0) {
                // If user is a participant, update participant table
                if (user instanceof participant) {
                    String reqParticipant = "UPDATE participants SET nombreParticipations = ? WHERE id_user = ?";
                    PreparedStatement stmParticipant = cnx.prepareStatement(reqParticipant);
                    stmParticipant.setInt(1, ((participant) user).getNombreParticipations());
                    stmParticipant.setInt(2, user.getId_user());
                    stmParticipant.executeUpdate();
                }

                // If user is a partenaire, update partenaire table
                if (user instanceof partenaire) {
                    String reqPartenaire = "UPDATE partenaire SET type_service = ?, site_web = ?, nbre_contacts = ? WHERE id_user = ?";
                    PreparedStatement stmPartenaire = cnx.prepareStatement(reqPartenaire);
                    stmPartenaire.setString(1, ((partenaire) user).getTypeService());
                    stmPartenaire.setString(2, ((partenaire) user).getSiteWeb());
                    stmPartenaire.setInt(3, ((partenaire) user).getNbreContrats());
                    stmPartenaire.setInt(4, user.getId_user());
                    stmPartenaire.executeUpdate();
                }

                // If user is an organisateur, update organisateur table
                if (user instanceof organisateur) {
                    String reqOrganisateur = "UPDATE organisateur SET workField = ?, workEmail = ? WHERE id_user = ?";
                    PreparedStatement stmOrganisateur = cnx.prepareStatement(reqOrganisateur);
                    stmOrganisateur.setString(1, ((organisateur) user).getWorkField());
                    stmOrganisateur.setString(2, ((organisateur) user).getWorkEmail());
                    stmOrganisateur.setInt(3, user.getId_user());
                    stmOrganisateur.executeUpdate();
                }

                // If user is an admin, update admin table
                if (user instanceof admin) {
                    String reqAdmin = "UPDATE admin SET createdAt = ? WHERE id_user = ?";
                    PreparedStatement stmAdmin = cnx.prepareStatement(reqAdmin);
                    stmAdmin.setDate(1, java.sql.Date.valueOf(((admin) user).getCreatedAt()));
                    stmAdmin.setInt(2, user.getId_user());
                    stmAdmin.executeUpdate();
                }

                System.out.println("Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        try {
            // Step 1: Delete from dependent tables first to maintain referential integrity
            String[] deleteQueries = {
                    "DELETE FROM participants WHERE id_user = ?",
                    "DELETE FROM partenaire WHERE id_user = ?",
                    "DELETE FROM organisateur WHERE id_user = ?",
                    "DELETE FROM admin WHERE id_user = ?"
            };

            for (String query : deleteQueries) {
                PreparedStatement stm = cnx.prepareStatement(query);
                stm.setInt(1, id);
                stm.executeUpdate();
            }

            // Step 2: Delete from the main user table
            String reqUser = "DELETE FROM user WHERE id_user = ?";
            PreparedStatement stmUser = cnx.prepareStatement(reqUser);
            stmUser.setInt(1, id);
            int rowsAffected = stmUser.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Utilisateur supprimé avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public List<user> getall() {
        List<user> users = new ArrayList<>();
        String reqUser = "SELECT * FROM user";

        try {
            PreparedStatement stmUser = cnx.prepareStatement(reqUser);
            ResultSet rsUser = stmUser.executeQuery();

            while (rsUser.next()) {
                int id = rsUser.getInt("id_user");
                String nom = rsUser.getString("nom");
                String prenom = rsUser.getString("prenom");
                String email = rsUser.getString("email");
                String motDePasse = rsUser.getString("mot_de_passe");
                Date dateNaiss = rsUser.getDate("date_naiss");
                String adresse = rsUser.getString("adresse");
                int telephone = rsUser.getInt("telephone");
                Date dateInscription = rsUser.getDate("date_inscription");

                user u = new user(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate());

                // ✅ Assign id_user to the object
                u.setId_user(id);

                // Check if user is a participant
                String reqParticipant = "SELECT nombreParticipations FROM participants WHERE id_user = ?";
                PreparedStatement stmParticipant = cnx.prepareStatement(reqParticipant);
                stmParticipant.setInt(1, id);
                ResultSet rsParticipant = stmParticipant.executeQuery();
                if (rsParticipant.next()) {
                    int nombreParticipations = rsParticipant.getInt("nombreParticipations");
                    u = new participant(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), nombreParticipations);
                }

                // Check if user is a partenaire
                String reqPartenaire = "SELECT type_service, site_web, nbre_contacts FROM partenaire WHERE id_user = ?";
                PreparedStatement stmPartenaire = cnx.prepareStatement(reqPartenaire);
                stmPartenaire.setInt(1, id);
                ResultSet rsPartenaire = stmPartenaire.executeQuery();
                if (rsPartenaire.next()) {
                    String typeService = rsPartenaire.getString("type_service");
                    String siteWeb = rsPartenaire.getString("site_web");
                    int nbreContrats = rsPartenaire.getInt("nbre_contacts");
                    u = new partenaire(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), typeService, siteWeb, nbreContrats);
                }

                // Check if user is an organisateur
                String reqOrganisateur = "SELECT workField, workEmail FROM organisateur WHERE id_user = ?";
                PreparedStatement stmOrganisateur = cnx.prepareStatement(reqOrganisateur);
                stmOrganisateur.setInt(1, id);
                ResultSet rsOrganisateur = stmOrganisateur.executeQuery();
                if (rsOrganisateur.next()) {
                    String workField = rsOrganisateur.getString("workField");
                    String workEmail = rsOrganisateur.getString("workEmail");
                    u = new organisateur(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), workField, workEmail);
                }

                // Check if user is an admin
                String reqAdmin = "SELECT createdAt FROM admin WHERE id_user = ?";
                PreparedStatement stmAdmin = cnx.prepareStatement(reqAdmin);
                stmAdmin.setInt(1, id);
                ResultSet rsAdmin = stmAdmin.executeQuery();
                if (rsAdmin.next()) {
                    LocalDate createdAt = rsAdmin.getDate("createdAt").toLocalDate();
                    u = new admin(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), createdAt);
                }

                // ✅ Ensure id_user is still set after type conversion
                u.setId_user(id);

                users.add(u);
            }

            for (user u : users) {
                System.out.println("User ID: " + u.getId_user() + ", Name: " + u.getNom());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return users;
    }

    @Override
    public user getOne(int id) {
        String reqUser = "SELECT * FROM user WHERE id_user = ?";
        user u = null;

        try {
            PreparedStatement stmUser = cnx.prepareStatement(reqUser);
            stmUser.setInt(1, id);
            ResultSet rsUser = stmUser.executeQuery();

            if (rsUser.next()) {
                int idUser = rsUser.getInt("id_user");  // ✅ Get the ID
                String nom = rsUser.getString("nom");
                String prenom = rsUser.getString("prenom");
                String email = rsUser.getString("email");
                String motDePasse = rsUser.getString("mot_de_passe");
                Date dateNaiss = rsUser.getDate("date_naiss");
                String adresse = rsUser.getString("adresse");
                int telephone = rsUser.getInt("telephone");
                Date dateInscription = rsUser.getDate("date_inscription");

                // ✅ Create the user object with the correct ID
                u = new user(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate());
                u.setId_user(idUser);  // ✅ Set the ID!

                // Check if user is a participant
                String reqParticipant = "SELECT nombreParticipations FROM participants WHERE id_user = ?";
                PreparedStatement stmParticipant = cnx.prepareStatement(reqParticipant);
                stmParticipant.setInt(1, id);
                ResultSet rsParticipant = stmParticipant.executeQuery();
                if (rsParticipant.next()) {
                    int nombreParticipations = rsParticipant.getInt("nombreParticipations");
                    u = new participant(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), nombreParticipations);
                    u.setId_user(idUser);  // ✅ Keep the ID!
                }

                // Check if user is a partenaire
                String reqPartenaire = "SELECT type_service, site_web, nbre_contacts FROM partenaire WHERE id_user = ?";
                PreparedStatement stmPartenaire = cnx.prepareStatement(reqPartenaire);
                stmPartenaire.setInt(1, id);
                ResultSet rsPartenaire = stmPartenaire.executeQuery();
                if (rsPartenaire.next()) {
                    String typeService = rsPartenaire.getString("type_service");
                    String siteWeb = rsPartenaire.getString("site_web");
                    int nbreContrats = rsPartenaire.getInt("nbre_contacts");
                    u = new partenaire(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), typeService, siteWeb, nbreContrats);
                    u.setId_user(idUser);
                }

                // Check if user is an organisateur
                String reqOrganisateur = "SELECT workField, workEmail FROM organisateur WHERE id_user = ?";
                PreparedStatement stmOrganisateur = cnx.prepareStatement(reqOrganisateur);
                stmOrganisateur.setInt(1, id);
                ResultSet rsOrganisateur = stmOrganisateur.executeQuery();
                if (rsOrganisateur.next()) {
                    String workField = rsOrganisateur.getString("workField");
                    String workEmail = rsOrganisateur.getString("workEmail");
                    u = new organisateur(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), workField, workEmail);
                    u.setId_user(idUser);
                }

                // Check if user is an admin
                String reqAdmin = "SELECT createdAt FROM admin WHERE id_user = ?";
                PreparedStatement stmAdmin = cnx.prepareStatement(reqAdmin);
                stmAdmin.setInt(1, id);
                ResultSet rsAdmin = stmAdmin.executeQuery();
                if (rsAdmin.next()) {
                    LocalDate createdAt = rsAdmin.getDate("createdAt").toLocalDate();
                    u = new admin(nom, prenom, email, motDePasse, dateNaiss.toLocalDate(), adresse, telephone, dateInscription.toLocalDate(), createdAt);
                    u.setId_user(idUser);
                }

                System.out.println("User found: " + idUser + " - " + nom);
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }

        return u;
    }



}



