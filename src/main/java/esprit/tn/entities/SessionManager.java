package esprit.tn.entities;

import java.io.*;

public class SessionManager {
    private static final String TOKEN_FILE = "token.txt";
    private static String token;

    public static void setToken(String newToken) {
        token = newToken;

        // Prevent writing null tokens to the file
        if (newToken == null || newToken.isEmpty()) {
            deleteTokenFile();
        } else {
            saveTokenToFile(newToken);
        }
    }

    public static String getToken() {
        if (token == null) {
            token = loadTokenFromFile();
        }
        return token;
    }

    public static void clearToken() {
        token = null;
        deleteTokenFile();
    }

    private static void saveTokenToFile(String token) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TOKEN_FILE))) {
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadTokenFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TOKEN_FILE))) {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    private static void deleteTokenFile() {
        File file = new File(TOKEN_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("🗑 Token file deleted.");
        }
    }
}
