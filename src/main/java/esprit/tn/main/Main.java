package esprit.tn.main;

import esprit.tn.entities.*;
import esprit.tn.services.authentificationService;
import esprit.tn.services.userService;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create instances of services
        userService userServiceInstance = new userService();
        authentificationService authService = new authentificationService();



            List<user> users = userServiceInstance.getByName("emna");

            if (!users.isEmpty()) {
                System.out.println("✅ Users found:");
                for (user u : users) {
                    System.out.println(u);
                }
            } else {
                System.out.println("❌ No users found with the given name.");
            }

        // Uncomment to test password hashing
        // String plainPassword = "TopAdmin123";
        // String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        // System.out.println("Hashed Password: " + hashedPassword);

        // Uncomment to test password update
        // String userEmail = "Emna.MESSAOUDI@esprit.tn";
        // String newPassword = "NewSecurePass@123";
        // boolean isUpdated = authService.updatePassword(userEmail, newPassword);
        // System.out.println(isUpdated ? "✅ Password updated successfully" : "❌ Failed to update password.");
    }
}
