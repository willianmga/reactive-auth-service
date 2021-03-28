package live.socialchat.auth.session;

import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.user.model.User;
import reactor.core.publisher.Mono;

public interface SessionRepository {
    void authenticate(ChatSession chatSession, User user, String token);
    Mono<Boolean> logoff(String sessionId);
}
