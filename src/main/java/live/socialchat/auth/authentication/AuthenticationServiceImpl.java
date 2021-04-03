package live.socialchat.auth.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.OffsetDateTime;
import java.util.UUID;
import live.socialchat.auth.authentication.model.AuthenticateRequest;
import live.socialchat.auth.authentication.model.AuthenticateResponse;
import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.authentication.model.ChatSession.Status;
import live.socialchat.auth.authentication.model.ServerResponse;
import live.socialchat.auth.authentication.model.ValidateTokenServerResponse;
import live.socialchat.auth.exception.ChatException;
import live.socialchat.auth.exception.ResponseStatus;
import live.socialchat.auth.secret.HashingService;
import live.socialchat.auth.session.SessionRepository;
import live.socialchat.auth.token.TokenService;
import live.socialchat.auth.token.model.CreateTokenResponse;
import live.socialchat.auth.user.UserRepository;
import live.socialchat.auth.user.model.User;
import live.socialchat.auth.user.model.UserDTO;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static live.socialchat.auth.exception.ResponseStatus.INVALID_CREDENTIALS;
import static live.socialchat.auth.token.JwtTokenService.SESSION_ID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    
    private final TokenService tokenService;
    private final HashingService hashingService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public AuthenticationServiceImpl(final TokenService tokenService,
                                     final HashingService hashingService,
                                     final UserRepository userRepository,
                                     final SessionRepository sessionRepository) {
        this.tokenService = tokenService;
        this.hashingService = hashingService;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    public Mono<AuthenticateResponse> authenticate(final AuthenticateRequest authenticateRequest) {
    
        return userRepository.findFullDetailsByUsername(authenticateRequest.getUsername())
            .switchIfEmpty(Mono.error(new ChatException("Invalid Credentials", INVALID_CREDENTIALS)))
            .handle((user, sink) -> {
    
                if (hashingService.matches(authenticateRequest.getPassword(), user.getPassword())) {
        
                    try {

                        final String sessionId = UUID.randomUUID().toString();
                        final CreateTokenResponse createTokenResponse = tokenService.create(sessionId, user);
    
                        final ChatSession newSession = ChatSession.builder()
                            .id(sessionId)
                            .userDeviceDetails(authenticateRequest.getUserDeviceDetails())
                            .startDate(OffsetDateTime.now().toString())
                            .expiryDate(createTokenResponse.getTokenExpireDate().toString())
                            .status(Status.AUTHENTICATED)
                            .build();
                        
                        sessionRepository.authenticate(newSession, user, createTokenResponse.getToken());
            
                        LOGGER.info("New session authenticated: {}", newSession.getId());
            
                        sink.next(
                            AuthenticateResponse.builder()
                                .user(mapToUserDTO(user))
                                .token(createTokenResponse.getToken())
                                .tokenExpireDate(createTokenResponse.getTokenExpireDate().toString())
                                .status(ResponseStatus.SUCCESS)
                                .build()
                        );
            
                    } catch (Exception e) {
                        sink.error(new ChatException("Failed to authenticate. Reason: " + e.getMessage(), ResponseStatus.SERVER_ERROR));
                    }
        
                } else {
                    sink.error(new ChatException("Invalid Credentials", INVALID_CREDENTIALS));
                }
             
            });
    
    }
    
    @Override
    public Mono<ValidateTokenServerResponse> validate(final String token) {
        return Mono.fromCallable(() -> {
            
            final Claims claims = tokenService.validate(token).getBody();
            
            return ValidateTokenServerResponse.builder()
                .sessionId(claims.get(SESSION_ID, String.class))
                .userId(claims.getSubject())
                .status(ResponseStatus.SUCCESS)
                .message("Token successfully validated")
                .build();
            
        });
    }
    
    @Override
    public Mono<ServerResponse> logoff(final String token) {
        return Mono.fromCallable(() -> tokenService.parse(token))
            .flatMap(claims -> Mono.just(claims.getBody().get(SESSION_ID, String.class)))
            .onErrorResume(this::getSessionId)
            .flatMap(sessionId -> (!Strings.isNullOrEmpty(sessionId))
                ? sessionRepository.logoff(sessionId)
                : Mono.just(Boolean.FALSE)
            )
            .flatMap(sessionUpdated -> (sessionUpdated)
                ? Mono.just(ServerResponse.success("Session successfully invalidated"))
                : Mono.just(ServerResponse.success("No sessions to invalidate"))
            );
    }
    
    private Mono<String> getSessionId(Throwable error) {
        if (error instanceof ExpiredJwtException) {
            ExpiredJwtException expiredJwtException = (ExpiredJwtException) error;
            return Mono.just(expiredJwtException.getClaims().get(SESSION_ID, String.class));
        }
        LOGGER.error("Failed to invalidate session because token is not valid");
        return Mono.just("");
    }
    
    private UserDTO mapToUserDTO(final User user) {
        return UserDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .description(user.getDescription())
            .avatar(user.getAvatar())
            .build();
    }
    
}
