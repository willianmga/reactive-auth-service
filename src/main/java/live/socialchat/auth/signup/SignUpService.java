package live.socialchat.auth.signup;

import live.socialchat.auth.authentication.model.AuthenticateResponse;
import reactor.core.publisher.Mono;

public interface SignUpService {
    Mono<AuthenticateResponse> signup(SignupRequest signupRequest);
}
