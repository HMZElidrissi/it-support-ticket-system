package ma.hmzelidrissi.frontend.utils;

import lombok.Getter;
import lombok.Setter;
import ma.hmzelidrissi.frontend.dto.AuthResponse;

@Setter
@Getter
public class AuthManager {
    private static AuthManager instance;

    private String authToken;
    private AuthResponse currentUser;

    private AuthManager() {}

    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }

    public void logout() {
        this.authToken = null;
        this.currentUser = null;
    }
}