package live.socialchat.auth.cookie;

import live.socialchat.auth.authentication.model.AuthenticateResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {
    
    public static final String B_COOKIE_NAME = "b";
    public static final String SET_COOKIE_HEADER_NAME = "Set-Cookie";
    
    private static final String B_COOKIE_DOMAIN = "b.cookie.domain";
    private static final String SET_COOKIE_FORMAT = B_COOKIE_NAME + "=%s; Path=/; Expires=%s; Domain=%s; SameSite=Strict; Secure; HttpOnly;";
    private static final String B_COOKIE_REVOKED_FORMAT = B_COOKIE_NAME + "=; Path=/; Max-Age=0; Domain=%s; SameSite=Strict; Secure; HttpOnly;";
    
    private final Environment environment;
    
    public CookieServiceImpl(final Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public String createBCookie(final AuthenticateResponse authenticateResponse) {
        return String.format(
            SET_COOKIE_FORMAT,
            authenticateResponse.getToken(),
            authenticateResponse.getTokenExpireDate(),
            environment.getProperty(B_COOKIE_DOMAIN)
        );
    }
    
    @Override
    public String revokeBCookie() {
        return String.format(
            B_COOKIE_REVOKED_FORMAT,
            environment.getProperty(B_COOKIE_DOMAIN)
        );
    }
    
}
