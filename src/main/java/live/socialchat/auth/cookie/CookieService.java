package live.socialchat.auth.cookie;

import live.socialchat.auth.authentication.model.AuthenticateResponse;

public interface CookieService {
    String createBCookie(AuthenticateResponse authenticateResponse);
    String revokeBCookie();
}
