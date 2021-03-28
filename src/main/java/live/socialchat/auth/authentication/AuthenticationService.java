package live.socialchat.auth.authentication;

import live.socialchat.auth.authentication.model.AuthenticateRequest;
import live.socialchat.auth.authentication.model.AuthenticateResponse;
import live.socialchat.auth.authentication.model.ServerResponse;
import live.socialchat.auth.authentication.model.ValidateTokenServerResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<AuthenticateResponse> authenticate(AuthenticateRequest authenticateRequest);
    Mono<ValidateTokenServerResponse> validate(String token);
    Mono<ServerResponse> logoff(String value);
}
