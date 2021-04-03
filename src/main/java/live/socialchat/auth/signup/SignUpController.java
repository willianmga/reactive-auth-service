package live.socialchat.auth.signup;

import javax.servlet.http.HttpServletResponse;
import live.socialchat.auth.authentication.model.AuthenticateResponse;
import live.socialchat.auth.signup.model.SignupRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface SignUpController {
    Mono<ResponseEntity<AuthenticateResponse>> signup(SignupRequest signupRequest, HttpServletResponse response);
}
