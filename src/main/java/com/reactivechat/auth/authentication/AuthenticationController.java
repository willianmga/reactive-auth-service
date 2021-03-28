package com.reactivechat.auth.authentication;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import com.reactivechat.auth.authentication.model.ValidateTokenServerResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthenticationController {
    Mono<ResponseEntity<AuthenticateResponse>> authenticate(AuthenticateRequest authenticateRequest, HttpServletResponse response);
    Mono<ResponseEntity<ValidateTokenServerResponse>> validateToken(String token);
}
