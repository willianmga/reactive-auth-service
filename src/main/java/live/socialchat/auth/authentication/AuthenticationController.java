package live.socialchat.auth.authentication;

import live.socialchat.auth.authentication.model.AuthenticateRequest;
import live.socialchat.auth.authentication.model.AuthenticateResponse;
import live.socialchat.auth.authentication.model.ValidateTokenServerResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthenticationController {
    Mono<ResponseEntity<AuthenticateResponse>> authenticate(AuthenticateRequest authenticateRequest, HttpServletResponse response);
    Mono<ResponseEntity<ValidateTokenServerResponse>> validateToken(String token);
}
