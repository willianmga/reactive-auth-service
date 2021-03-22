package com.reactivechat.auth.session;

import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.user.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionRepository {
    
    void authenticate(ChatSession chatSession, User user, String token);
    Mono<String> reauthenticate(ChatSession chatSession, String token);
    Mono<Boolean> deleteConnection(String connectionId);
    void logoff(ChatSession chatSession);
    Flux<ChatSession> findByUser(String userId);
    Flux<ChatSession> findAllConnections();
    Mono<ChatSession> findByActiveToken(String token);
    Mono<ChatSession> tokenInUse(String token);
    
}