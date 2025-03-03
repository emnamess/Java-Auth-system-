package esprit.tn.main;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import esprit.tn.entities.SessionManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // 🔹 Generate a token with a short expiration (e.g., 10 seconds)
        String validToken = JWT.create()
                .withClaim("userId", 123)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10000)) // Expires in 10 sec
                .sign(Algorithm.HMAC256("secret"));

        System.out.println("🟢 Generated Token: " + validToken);

        // 🔹 Save token in SessionManager
        SessionManager.setToken(validToken);

        // 🔍 Check token expiration every second
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1); // Wait 1 second
                long remainingTime = getRemainingTime(SessionManager.getToken());

                if (remainingTime > 0) {
                    System.out.println("⏳ Token expires in " + remainingTime + " seconds...");
                } else {
                    System.out.println("❌ Token has expired!");
                    break; // Exit loop when expired
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // ✅ Helper method to check remaining time
    public static long getRemainingTime(String token) {
        if (token == null) return 0;

        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            long expiration = decodedJWT.getExpiresAt().getTime();
            long currentTime = System.currentTimeMillis();

            return (expiration - currentTime) / 1000; // Convert to seconds
        } catch (Exception e) {
            return 0; // Assume expired if error occurs
        }
    }
}
