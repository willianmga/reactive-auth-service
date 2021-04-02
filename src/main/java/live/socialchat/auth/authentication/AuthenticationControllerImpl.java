package live.socialchat.auth.authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import live.socialchat.auth.authentication.model.AuthenticateRequest;
import live.socialchat.auth.authentication.model.AuthenticateResponse;
import live.socialchat.auth.authentication.model.ServerResponse;
import live.socialchat.auth.authentication.model.ValidateTokenServerResponse;
import live.socialchat.auth.cookie.CookieService;
import live.socialchat.auth.exception.ChatException;
import live.socialchat.auth.exception.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static live.socialchat.auth.cookie.CookieServiceImpl.B_COOKIE_NAME;
import static live.socialchat.auth.cookie.CookieServiceImpl.SET_COOKIE_HEADER_NAME;

@CrossOrigin
@RestController
@RequestMapping("/v1/auth")
public class AuthenticationControllerImpl implements AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationControllerImpl.class);
    
    private final AuthenticationService authenticationService;
    private final CookieService cookieService;

    @Autowired
    public AuthenticationControllerImpl(final AuthenticationServiceImpl authenticationService,
                                        final CookieService cookieService) {
        this.authenticationService = authenticationService;
        this.cookieService = cookieService;
    }

    @PostMapping
    public Mono<ResponseEntity<AuthenticateResponse>> authenticate(@RequestBody AuthenticateRequest authenticateRequest,
                                                                   HttpServletResponse response) {
    
        return authenticationService.authenticate(authenticateRequest)
            .flatMap(authenticateResponse -> {

                response.addHeader(SET_COOKIE_HEADER_NAME, cookieService.createBCookie(authenticateResponse));
                
                LOGGER.info("User {} successfully logged in", authenticateRequest.getUsername());
    
                return Mono.just(ResponseEntity
                        .ok(AuthenticateResponse.builder()
                            .user(authenticateResponse.getUser())
                            .status(authenticateResponse.getStatus())
                            .tokenExpireDate(authenticateResponse.getTokenExpireDate())
                            .build()
                        )
                    );
            })
            .onErrorResume((error) -> {
                ChatException chatException = (ChatException) error;
                HttpStatus httpStatus;
                ResponseStatus responseStatus;
                
                if (chatException.getResponseStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                    LOGGER.info("Failed to login user {} with invalid credentials", authenticateRequest.getUsername());
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    responseStatus = ResponseStatus.INVALID_CREDENTIALS;
                } else {
                    LOGGER.error("Server error: {}", error.getMessage());
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    responseStatus = ResponseStatus.SERVER_ERROR;
                }
    
                return Mono.just(ResponseEntity
                    .status(httpStatus)
                    .body(AuthenticateResponse.builder()
                        .status(responseStatus)
                        .build()
                    )
                );
                
            });
    }

    @PostMapping
    @RequestMapping("/logoff")
    public Mono<ServerResponse> logOff(@CookieValue(name = B_COOKIE_NAME) Cookie bCookie,
                                       HttpServletResponse servletResponse) {
        return authenticationService.logoff(bCookie.getValue())
            .flatMap(response -> {
                servletResponse.addHeader(SET_COOKIE_HEADER_NAME, cookieService.revokeBCookie());
                return Mono.just(response);
            });
    }
    
    @PostMapping
    @RequestMapping("/token/valid")
    public Mono<ResponseEntity<ValidateTokenServerResponse>> validateToken(@RequestHeader("Authorization") final String token) {
        
        return authenticationService.validate(token)
            .flatMap((response) -> Mono.just(ResponseEntity.ok(response)))
            .onErrorResume((error) -> {
                ChatException chatException = (ChatException) error;
                if (chatException.getResponseStatus() == ResponseStatus.EXPIRED_TOKEN) {
                    LOGGER.info("Failed to validate expired token");
                    return Mono.just(ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(error(chatException)));
                } else {
                    LOGGER.error("Fail while validating compromised token");
                    return Mono.just(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(error(chatException)));
                }
            });
        
    }
    
    private ValidateTokenServerResponse error(final ChatException chatException) {
        return ValidateTokenServerResponse.builder()
            .status(chatException.getResponseStatus())
            .message(chatException.getMessage())
            .build();
    }

}
