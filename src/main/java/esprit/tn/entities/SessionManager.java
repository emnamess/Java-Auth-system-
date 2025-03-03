package esprit.tn.entities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.*;
import java.time.Instant;

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

        if (token != null && isTokenExpired(token)) {
            System.out.println("❌ Token has expired! Clearing it.");
            clearToken(); // Automatically remove expired token
            return null;
        }

        return token;
    }

    public static Integer getUserIdFromToken() {
        String jwt = getToken();
        if (jwt == null || jwt.isEmpty()) {
            System.out.println("⚠ No JWT token found.");
            return null;
        }

        try {
            System.out.println("🔍 Decoding JWT: " + jwt); // Debug token content
            DecodedJWT decodedJWT = JWT.decode(jwt);

            if (decodedJWT.getClaim("userId").isNull()) {
                System.out.println("⚠ 'userId' claim not found in JWT.");
                return null;
            }

            Integer userId = decodedJWT.getClaim("userId").asInt();
            System.out.println("✅ Extracted userId: " + userId);
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

    private static boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Instant expirationTime = jwt.getExpiresAt().toInstant();
            Instant now = Instant.now();

            return now.isAfter(expirationTime); // If now > expiration, token is expired
        } catch (Exception e) {
            System.out.println("❌ Error checking token expiration: " + e.getMessage());
            return true; // If decoding fails, assume it's expired
        }
    }

    private static void saveTokenToFile(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.err.println("❌ Token is null or empty, skipping save.");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TOKEN_FILE))) {
            writer.write(token);
            System.out.println("✅ Token successfully saved to file: " + token); // Debug print
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadTokenFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TOKEN_FILE))) {
            String token = reader.readLine();
            System.out.println("📂 Token read from file: " + token);
            return token;
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
    public static long getRemainingSessionTime() {
        String jwt = getToken();
        if (jwt == null) return 0;

        try {
            DecodedJWT decodedJWT = JWT.decode(jwt);
            Instant expirationTime = decodedJWT.getExpiresAt().toInstant();
            Instant now = Instant.now();

            long remainingTime = expirationTime.getEpochSecond() - now.getEpochSecond();
            System.out.println("⏳ Time left until session expires: " + remainingTime + " seconds");
            return remainingTime;
        } catch (Exception e) {
            System.out.println("❌ Error calculating remaining time: " + e.getMessage());
            return 0;
        }
    }

}
