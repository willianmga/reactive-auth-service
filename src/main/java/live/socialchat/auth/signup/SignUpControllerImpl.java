package live.socialchat.auth.signup;

import javax.servlet.http.HttpServletResponse;
import live.socialchat.auth.authentication.model.AuthenticateResponse;
import live.socialchat.auth.cookie.CookieService;
import live.socialchat.auth.signup.model.SignupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static live.socialchat.auth.cookie.CookieServiceImpl.SET_COOKIE_HEADER_NAME;

@CrossOrigin
@RestController
@RequestMapping("/v1/signup")
public class SignUpControllerImpl implements SignUpController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignUpControllerImpl.class);
    
    private final SignUpService signUpService;
    private final CookieService cookieService;
    
    @Autowired
    private SignUpControllerImpl(final SignUpService signUpService,
                                 final CookieService cookieService) {
        this.signUpService = signUpService;
        this.cookieService = cookieService;
    }
    
    @PostMapping
    public Mono<ResponseEntity<AuthenticateResponse>> signup(final @RequestBody SignupRequest signupRequest,
                                                             final HttpServletResponse response) {
        return signUpService.signup(signupRequest)
            .flatMap(authenticateResponse -> {
    
                response.addHeader(SET_COOKIE_HEADER_NAME, cookieService.createBCookie(authenticateResponse));
    
                LOGGER.info("User {} successfully created and logged in", signupRequest.getUsername());
    
                return Mono.just(ResponseEntity
                    .ok(AuthenticateResponse.builder()
                        .user(authenticateResponse.getUser())
                        .status(authenticateResponse.getStatus())
                        .tokenExpireDate(authenticateResponse.getTokenExpireDate())
                        .build()
                    )
                );

            });
    }
    
    
}
