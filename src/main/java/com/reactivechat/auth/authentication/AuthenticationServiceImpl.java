package com.reactivechat.auth.authentication;

import com.reactivechat.auth.session.SessionRepository;
import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.exception.ChatException;
import com.reactivechat.auth.exception.ResponseStatus;
import com.reactivechat.auth.token.TokenService;
import com.reactivechat.auth.user.UserRepository;
import com.reactivechat.auth.user.model.User;
import com.reactivechat.auth.user.model.UserDTO;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.reactivechat.auth.authentication.model.ChatSession.Status.AUTHENTICATED;
import static com.reactivechat.auth.exception.ResponseStatus.INVALID_CREDENTIALS;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public AuthenticationServiceImpl(TokenService tokenService,
                                     UserRepository userRepository,
                                     SessionRepository sessionRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    public Mono<AuthenticateResponse> authenticate(AuthenticateRequest authenticateRequest) {
    
        return userRepository.findFullDetailsByUsername(authenticateRequest.getUsername())
            .switchIfEmpty(Mono.error(new ChatException("Invalid Credentials", INVALID_CREDENTIALS)))
            .handle((user, sink) -> {
    
                if (user.getPassword().equals(authenticateRequest.getPassword())) {
        
                    try {
            
                        final ChatSession newSession = ChatSession.builder()
                            .id(UUID.randomUUID().toString())
                            .userDeviceDetails(authenticateRequest.getUserDeviceDetails())
                            .startDate(OffsetDateTime.now().toString())
                            .status(AUTHENTICATED)
                            .build();
            
                        final String token = tokenService.create(newSession, user);
                        sessionRepository.authenticate(newSession, user, token);
            
                        LOGGER.info("New session authenticated: {}", newSession.getId());
            
                        sink.next(
                            AuthenticateResponse.builder()
                                .user(mapToUserDTO(user))
                                .token(token)
                                .status(ResponseStatus.SUCCESS)
                                .build()
                        );
            
                    } catch (Exception e) {
                        sink.error(new ChatException("Failed to authenticate. Reason: " + e.getMessage(), ResponseStatus.SERVER_ERROR));
                    }
        
                }
             
                sink.error(new ChatException("Invalid Credentials", INVALID_CREDENTIALS));
                
            });
    
    }
    
    @Override
    public Mono<Boolean> validate(final String token) {
        return Mono.fromCallable(() -> tokenService.validate(token));
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
