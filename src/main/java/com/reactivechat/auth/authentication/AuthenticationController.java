package com.reactivechat.auth.authentication;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import javax.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationController {
    
    Mono<AuthenticateResponse> authenticate(AuthenticateRequest authenticateRequest, HttpServletResponse response);
}
