package esprit.tn.entities;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
public class JwtUtils {



        public static String extractRole(String token) {
            try {
                DecodedJWT jwt = JWT.decode(token);
                return jwt.getClaim("role").asString(); // Get the role from the token
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

