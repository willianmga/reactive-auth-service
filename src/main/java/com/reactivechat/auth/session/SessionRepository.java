package com.reactivechat.auth.session;

import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.user.model.User;
import reactor.core.publisher.Mono;

public interface SessionRepository {
    void authenticate(ChatSession chatSession, User user, String token);
    Mono<Boolean> logoff(String sessionId);
}
