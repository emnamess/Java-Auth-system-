package esprit.tn.main;

import esprit.tn.entities.*;
import esprit.tn.services.authentificationService;
import esprit.tn.services.userService;
import esprit.tn.services.ChatService;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create instances of services
        userService userServiceInstance = new userService();
        authentificationService authService = new authentificationService();
        ChatService chatService = new ChatService();

        // Send a message
        Chat newMessage = new Chat(13, 17, "Hello, how are you?");
        chatService.sendMessage(newMessage);
        System.out.println("Message sent!");

        // Retrieve messages
        List<Chat> chatHistory = chatService.getMessages(13, 17);
        for (Chat chat : chatHistory) {
            System.out.println(chat.getSenderId() + " -> " + chat.getReceiverId() + ": " + chat.getMessageText());
        }

        // Update message status
        if (!chatHistory.isEmpty()) {
            chatService.updateMessageStatus(chatHistory.get(0).getId(), "seen");
            System.out.println("Message status updated to seen.");
        }

//        // Delete a message
//        if (!chatHistory.isEmpty()) {
//            chatService.deleteMessage(chatHistory.get(0).getId());
//            System.out.println("Message deleted.");
//        }

    }

}


//            List<user> users = userServiceInstance.getByName("emna");
//
//            if (!users.isEmpty()) {
//                System.out.println("✅ Users found:");
//                for (user u : users) {
//                    System.out.println(u);
//                }
//            } else {
//                System.out.println("❌ No users found with the given name.");
//            }

        // Uncomment to test password hashing
        // String plainPassword = "TopAdmin123";
        // String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        // System.out.println("Hashed Password: " + hashedPassword);

        // Uncomment to test password update
        // String userEmail = "Emna.MESSAOUDI@esprit.tn";
        // String newPassword = "NewSecurePass@123";
        // boolean isUpdated = authService.updatePassword(userEmail, newPassword);
        // System.out.println(isUpdated ? "✅ Password updated successfully" : "❌ Failed to update password.");


