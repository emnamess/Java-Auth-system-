package esprit.tn.main;

import esprit.tn.entities.*;
import esprit.tn.services.userService;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.getInstance();
        userService userservice = new userService();

        //user participant = new participant("Alice", "Durand", "alice@example.com", "password123",LocalDate.of(1995, 5, 20), "123 Rue des Fleurs", 123456789, LocalDate.now(),50);
        //user partenaire = new partenaire("emna", "messaoudi", "emna@example.com", "password123",LocalDate.of(1995, 5, 20), "123 Rue des zombies", 123456789, LocalDate.now(),"service1","www.service.com",44);
        //user organisateur = new organisateur("emna", "messaoudi", "emna@example.com", "password123",LocalDate.of(1995, 5, 20), "123 Rue des zombies", 123456789, LocalDate.now(),"it","it@example.com");
        //user admin = new admin("emna", "messaoudi", "emna@example.com", "password123", LocalDate.of(1995, 5, 20), "123 Rue des zombies", 123456789, LocalDate.now(),LocalDate.of(2001,12,01));
        //userservice.ajouter(admin);
        //user updatedUser = new participant(1,"Smith", "Jane", "jane@example.com", "newpassword123", LocalDate.of(1993, 8, 25), "456 New Street, City", 98765432, LocalDate.of(2001,01,04),100);
        //userservice.modifier(updatedUser);
        //userservice.supprimer(1);
        //userservice.getall();
        userservice.getOne(5);
    }

}
