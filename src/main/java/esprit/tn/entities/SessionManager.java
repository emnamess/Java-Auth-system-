package esprit.tn.entities;

public class SessionManager {
    private static String token; // Store the token in memory

    public static void setToken(String newToken) {
        token = newToken;
        System.out.println("Token Stored: " + token); // Debugging
    }

    public static String getToken() {
        return token;
    }
}

