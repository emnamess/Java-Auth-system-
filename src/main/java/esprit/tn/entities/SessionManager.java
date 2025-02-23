package esprit.tn.entities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.*;

public class SessionManager {
    private static final String TOKEN_FILE = "token.txt";
    private static String token;

    public static void setToken(String newToken) {
        token = newToken;
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
        System.out.println("🔍 Retrieved Token: " + token); // Debug print
        return token;
    }


    public static Integer getUserIdFromToken() {
        String jwt = getToken();
        if (jwt == null || jwt.isEmpty()) {
            System.out.println("⚠ No JWT token found.");
            return null;
        }

        try {
            DecodedJWT decodedJWT = JWT.decode(jwt);
            Integer userId = decodedJWT.getClaim("userId").asInt();
            System.out.println("✅ Extracted userId: " + userId); // Debug print
            return userId;
        } catch (Exception e) {
            System.out.println("❌ Error decoding JWT: " + e.getMessage());
            return null;
        }
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
