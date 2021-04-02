package live.socialchat.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import live.socialchat.auth.token.model.CreateTokenResponse;
import live.socialchat.auth.user.model.User;

public interface TokenService {
    CreateTokenResponse create(String sessionId, User user);
    Jws<Claims> validate(String token);
    Jws<Claims> parse(String token);
}
