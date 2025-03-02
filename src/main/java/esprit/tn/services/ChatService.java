package esprit.tn.services;

import esprit.tn.entities.Chat;
import esprit.tn.main.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private Connection connection;

    public ChatService() {
        connection = DatabaseConnection.getInstance().getCnx();
    }

    public void sendMessage(Chat chat) {
        String query = "INSERT INTO messages (sender_id, receiver_id, message_text, timestamp, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, chat.getSenderId());
            stmt.setInt(2, chat.getReceiverId());
            stmt.setString(3, chat.getMessageText());
            stmt.setTimestamp(4, chat.getTimestamp());
            stmt.setString(5, chat.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Chat> getMessages(int senderId, int receiverId) {
        List<Chat> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, receiverId);
            stmt.setInt(4, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new Chat(
                        rs.getInt("id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getString("message_text"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void updateMessageStatus(int messageId, String status) {
        String query = "UPDATE messages SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(int messageId) {
        String query = "DELETE FROM messages WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Chat> getUnreadMessages(int userId) {
        List<Chat> messages = new ArrayList<>();
        try {
            String query = """
            SELECT messages.sender_id, messages.message_text, user.nom, user.prenom
            FROM messages
            JOIN user ON messages.sender_id = user.Id_user
            WHERE messages.receiver_id = ? AND messages.status = 'unread'
        """;
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int senderId = rs.getInt("sender_id");
                String senderName = rs.getString("nom") + " " + rs.getString("prenom");  // Get full name
                String messageText = rs.getString("message_text");

                messages.add(new Chat(senderId, messageText, senderName)); // Update Chat model
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }


}


