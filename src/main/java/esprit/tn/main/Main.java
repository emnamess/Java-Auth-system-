package esprit.tn.main;

import esprit.tn.entities.*;
import esprit.tn.services.authentificationService;
import esprit.tn.services.userService;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseConnection.getInstance();
        userService userservice = new userService();
        authentificationService authService = new authentificationService();

        String userEmail = "Emna.MESSAOUDI@esprit.tn"; // Change to an existing user's email
        String newPassword = "NewSecurePass@123";

        System.out.println("[DEBUG] Attempting to update password for: " + userEmail);

        boolean isUpdated = authService.updatePassword(userEmail, newPassword);

        if (isUpdated) {
            System.out.println("✅ Password updated successfully for " + userEmail);
        } else {
            System.out.println("❌ Failed to update password.");
        }

        //user participant = new participant("Alice", "Durand", "alice@example.com", "password123",LocalDate.of(1995, 5, 20), "123 Rue des Fleurs", 123456789, LocalDate.now(),50);
        //user partenaire = new partenaire("emna", "messaoudi", "emna@example.com", "password123",LocalDate.of(1995, 5, 20), "123 Rue des zombies", 123456789, LocalDate.now(),"service1","www.service.com",44);
        //user organisateur = new organisateur("emna", "messaoudi", "emna@example.com", "password123",LocalDate.of(1995, 5, 20), "123 Rue des zombies", 123456789, LocalDate.now(),"it","it@example.com");
        //user admin = new admin("emna", "messaoudi", "emna@example.com", "password123", LocalDate.of(1995, 5, 20), "123 Rue des zombies", 123456789, LocalDate.now(),LocalDate.of(2001,12,01));
        //userservice.ajouter(organisateur);
//        user existingUser = userservice.getOne(6);
//        if (existingUser != null) {
//            // Modify some attributes
//            existingUser.setNom("NouveauNom");
//            existingUser.setPrenom("NouveauPrenom");
//            existingUser.setEmail("nouveauemail@example.com");
//
//            // Call the update method
//            userservice.modifier(existingUser);
//
//            // Verify update
//            user updatedUser = userservice.getOne(6);
//            System.out.println("Mise à jour terminée : " + updatedUser.getNom() + " " + updatedUser.getPrenom());
//        } else {
//            System.out.println("Utilisateur non trouvé !");
//        }
        //userservice.supprimer(2);
        //userservice.getall();
        //userservice.getOne(5);
    }}