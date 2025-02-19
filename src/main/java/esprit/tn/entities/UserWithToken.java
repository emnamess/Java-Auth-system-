package esprit.tn.entities;

public class UserWithToken {
    private user user;
    private String token;

    public UserWithToken(user user, String token) {
        this.user = user;
        this.token = token;
    }

    public user getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}

