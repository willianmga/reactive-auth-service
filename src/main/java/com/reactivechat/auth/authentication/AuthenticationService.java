package com.reactivechat.auth.authentication;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    
    Mono<AuthenticateResponse> authenticate(AuthenticateRequest authenticateRequest);
    Mono<Boolean> validate(String token);
    
}
