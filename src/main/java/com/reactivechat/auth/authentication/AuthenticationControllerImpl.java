package com.reactivechat.auth.authentication;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import com.reactivechat.auth.authentication.model.ServerResponse;
import com.reactivechat.auth.exception.ChatException;
import com.reactivechat.auth.exception.ResponseStatus;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/v1/auth")
public class AuthenticationControllerImpl implements AuthenticationController {
    
    private static final String B_COOKIE_DOMAIN = "b.cookie.domain";
    private static final String SET_COOKIE_HEADER_NAME = "Set-Cookie";
    private static final String SET_COOKIE_FORMAT = "b=%s; Path=/; Domain=%s; SameSite=Strict; Secure; HttpOnly;";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationControllerImpl.class);
    
    private final Environment environment;
    private final AuthenticationServiceImpl authenticationService;

    @Autowired
    public AuthenticationControllerImpl(AuthenticationServiceImpl authenticationService,
                                        Environment environment) {
        this.authenticationService = authenticationService;
        this.environment = environment;
    }

    @PostMapping
    public Mono<ResponseEntity<AuthenticateResponse>> authenticate(@RequestBody AuthenticateRequest authenticateRequest,
                                                                   HttpServletResponse response) {
    
        return authenticationService.authenticate(authenticateRequest)
            .flatMap(authenticateResponse -> {

                final String cookieDomain = String.format(
                    SET_COOKIE_FORMAT,
                    authenticateResponse.getToken(),
                    environment.getProperty(B_COOKIE_DOMAIN)
                );
    
                response.addHeader(SET_COOKIE_HEADER_NAME, cookieDomain);
                
                LOGGER.info("User {} successfully logged in", authenticateRequest.getUsername());
    
                return Mono.just(ResponseEntity
                        .ok(AuthenticateResponse.builder()
                                .user(authenticateResponse.getUser())
                                .status(authenticateResponse.getStatus())
                                .build()
                        )
                    );
            })
            .onErrorResume((error) -> {
                ChatException chatException = (ChatException) error;
                if (chatException.getResponseStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                    LOGGER.info("Failed to login user {} with invalid credentials", authenticateRequest.getUsername());
                    return Mono.just(
                        ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(AuthenticateResponse.builder()
                                .status(ResponseStatus.INVALID_CREDENTIALS)
                                .build()
                            )
                    );
                } else {
                    LOGGER.error("Server error: {}", error.getMessage());
                    return Mono.just(
                        ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(AuthenticateResponse.builder()
                                .status(ResponseStatus.SERVER_ERROR)
                                .build()
                            )
                    );
                }
            });
    }
    
    @PostMapping
    @RequestMapping("/token/valid")
    public Mono<ResponseEntity<ServerResponse>> validateToken(@RequestHeader("Authorization") final String token) {
        
        return authenticationService.validate(token)
            .flatMap((response) -> Mono.just(ResponseEntity.ok(ServerResponse.success("Token successfully validated"))))
            .onErrorResume((error) -> {
                ChatException chatException = (ChatException) error;
                if (chatException.getResponseStatus() == ResponseStatus.EXPIRED_TOKEN) {
                    LOGGER.info("Failed to validate expired token");
                    return Mono.just(ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(chatException.toServerResponse()));
                } else {
                    LOGGER.error("Fail while validating compromised token");
                    return Mono.just(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(chatException.toServerResponse()));
                }
            });
        
    }

}
