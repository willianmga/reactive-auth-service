package com.reactivechat.auth.authentication;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/v1/auth")
public class AuthenticationControllerImpl implements AuthenticationController {

    private static final String SET_COOKIE_FORMAT = "user_session=%s; Path=/; Domain=%s; SameSite=Strict; Secure; HttpOnly;";
    private static final String B_COOKIE_DOMAIN = "b.cookie.domain";
    
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationControllerImpl(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @GetMapping
    @RequestMapping("/test")
    // TODO: remove test endpoint
    public String test(HttpServletRequest request) {
        
        String value = (request.getCookies() != null)
            ? Arrays.toString(request.getCookies())
            : "NO-COOKIES";
        
        System.out.println(value);
        
        return "{}";
    }
    
    @PostMapping
    @RequestMapping("/test")
    // TODO: remove test endpoint
    public String tst(HttpServletRequest request) {
    
        String value = (request.getCookies() != null)
            ? Arrays.toString(request.getCookies())
            : "NO-COOKIES";
    
        System.out.println(value);
        
        return "{}";
    }
    
    @PostMapping
    public Mono<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest authenticateRequest,
                                                   HttpServletResponse response) {
    
        return authenticationService.authenticate(authenticateRequest)
            .map(authenticateResponse -> {

                final String cookieDomain = String.format(
                    SET_COOKIE_FORMAT,
                    authenticateResponse.getToken(),
                    "socialchat.com"
                    //System.getProperty(B_COOKIE_DOMAIN)
                );
    
                response.addHeader("Set-Cookie", cookieDomain);
                
                return AuthenticateResponse.builder()
                    .user(authenticateResponse.getUser())
                    .status(authenticateResponse.getStatus())
                    .build();
                }
            );
    }

}
