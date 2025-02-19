package esprit.tn.entities;
import java.io.*;
import java.nio.file.*;
public class TokenStorage {
    private static final String TOKEN_FILE = "token.txt";

    // Save the token in a file
    public static void saveToken(String token) {
        try {
            Files.write(Paths.get(TOKEN_FILE), token.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read the token from the file
    public static String getToken() {
        try {
            return new String(Files.readAllBytes(Paths.get(TOKEN_FILE)));
        } catch (IOException e) {
            return null;
        }
    }

    // Remove the token when logging out
    public static void clearToken() {
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}