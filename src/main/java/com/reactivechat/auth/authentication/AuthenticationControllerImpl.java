package com.reactivechat.auth.authentication;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import com.reactivechat.auth.authentication.model.ServerResponse;
import com.reactivechat.auth.authentication.model.ValidateTokenServerResponse;
import com.reactivechat.auth.exception.ChatException;
import com.reactivechat.auth.exception.ResponseStatus;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

@CrossOrigin
@RestController
@RequestMapping("/v1/auth")
public class AuthenticationControllerImpl implements AuthenticationController {
    
    private static final String B_COOKIE_NAME = "b";
    private static final String B_COOKIE_DOMAIN = "b.cookie.domain";
    private static final String SET_COOKIE_HEADER_NAME = "Set-Cookie";
    private static final String SET_COOKIE_FORMAT = B_COOKIE_NAME + "=%s; Path=/; Expires=%s; Domain=%s; SameSite=Strict; Secure; HttpOnly;";
    private static final String B_COOKIE_REVOKED_FORMAT = B_COOKIE_NAME + "=; Path=/; Max-Age=0; Domain=%s; SameSite=Strict; Secure; HttpOnly;";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationControllerImpl.class);
    
    private final Environment environment;
    private final AuthenticationService authenticationService;

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

                response.addHeader(SET_COOKIE_HEADER_NAME, createBCookie(authenticateResponse));
                
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
                servletResponse.addHeader(SET_COOKIE_HEADER_NAME, revokeBCookie());
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
    
    private String createBCookie(final AuthenticateResponse authenticateResponse) {
        return String.format(
            SET_COOKIE_FORMAT,
            authenticateResponse.getToken(),
            authenticateResponse.getTokenExpireDate(),
            environment.getProperty(B_COOKIE_DOMAIN)
        );
    }
    
    private String revokeBCookie() {
        return String.format(
            B_COOKIE_REVOKED_FORMAT,
            environment.getProperty(B_COOKIE_DOMAIN)
        );
    }
    
    private ValidateTokenServerResponse error(final ChatException chatException) {
        return ValidateTokenServerResponse.builder()
            .status(chatException.getResponseStatus())
            .message(chatException.getMessage())
            .build();
    }

}
