package live.socialchat.auth.token;

import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.token.model.CreateTokenResponse;
import live.socialchat.auth.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenService {
    CreateTokenResponse create(ChatSession session, User user);
    Jws<Claims> validate(String token);
    Jws<Claims> parse(String token);
}
