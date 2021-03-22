package com.reactivechat.auth.authentication.legacy;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.AuthenticateResponse;
import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.authentication.model.ReauthenticateRequest;
import com.reactivechat.auth.authentication.model.ServerDetails;
import com.reactivechat.auth.authentication.model.UserAuthenticationDetails;
import com.reactivechat.auth.authentication.response.MessageType;
import com.reactivechat.auth.authentication.response.ResponseMessage;
import com.reactivechat.auth.avatar.AvatarService;
import com.reactivechat.auth.avatar.AvatarServiceImpl;
import com.reactivechat.auth.exception.ChatException;
import com.reactivechat.auth.exception.ResponseStatus;
import com.reactivechat.auth.session.SessionRepository;
import com.reactivechat.auth.signup.SignupRequest;
import com.reactivechat.auth.user.UserRepository;
import com.reactivechat.auth.user.model.Contact.ContactType;
import com.reactivechat.auth.user.model.User;
import com.reactivechat.auth.user.model.UserDTO;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.reactivechat.auth.authentication.model.ChatSession.Status.AUTHENTICATED;
import static com.reactivechat.auth.authentication.model.ChatSession.Type.AUTHENTICATE;
import static com.reactivechat.auth.authentication.model.ChatSession.Type.REAUTHENTICATE;
import static com.reactivechat.auth.exception.ResponseStatus.INVALID_CREDENTIALS;
import static com.reactivechat.auth.exception.ResponseStatus.INVALID_NAME;
import static com.reactivechat.auth.exception.ResponseStatus.INVALID_PASSWORD;
import static com.reactivechat.auth.exception.ResponseStatus.INVALID_USERNAME;

public class AuthenticationControllerImpl implements AuthenticationController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationControllerImpl.class);
    private static final String DEFAULT_DESCRIPTION = "Hi, I'm using SocialChat!";
    private static final String TOKEN_SEPARATOR = "_";
    
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final AvatarService avatarService;
    private final ServerDetails serverDetails;
    
    @Autowired
    public AuthenticationControllerImpl(final UserRepository userRepository,
                                        final SessionRepository sessionRepository,
                                        final AvatarServiceImpl avatarController,
                                        final ServerDetails serverDetails) {
        
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.avatarService = avatarController;
        this.serverDetails = serverDetails;
    }
    
    @Override
    public void handleAuthenticate(final AuthenticateRequest authenticateRequest,
                                   final ChatSession chatSession) {
    
        try {
    
            final ResponseMessage<Object> responseMessage = ResponseMessage
                .builder()
                .type(MessageType.AUTHENTICATE)
                .payload(authenticate(authenticateRequest, chatSession))
                .build();
    
            //broadcasterController.broadcastToSession(chatSession, responseMessage);
            
        } catch (ChatException e) {
            //broadcasterController.broadcastToSession(chatSession, errorMessage(e, MessageType.AUTHENTICATE));
            LOGGER.error("Failed to authenticate user {}. Reason: {}", authenticateRequest.getUsername(), e.getMessage());
        }

    }
    
    @Override
    public void handleReauthenticate(final ReauthenticateRequest reauthenticateRequest, final ChatSession chatSession) {
    
        try {
    
            final ChatSession newSession = chatSession.from()
                .id(UUID.randomUUID().toString())
                .serverDetails(serverDetails)
                .startDate(OffsetDateTime.now().toString())
                .status(AUTHENTICATED)
                .type(REAUTHENTICATE)
                .build();
    
            final User user = sessionRepository.reauthenticate(newSession, reauthenticateRequest.getToken())
                .flatMap(userRepository::findById)
                .blockOptional()
                .orElseThrow(() -> new ChatException("Couldn't identify user for token", INVALID_CREDENTIALS));
    
            final ResponseMessage<Object> responseMessage = ResponseMessage
                .builder()
                .type(MessageType.REAUTHENTICATE)
                .payload(AuthenticateResponse.builder()
                    .user(mapToUserDTO(user))
                    .token(reauthenticateRequest.getToken())
                    .status(ResponseStatus.SUCCESS)
                    .build())
                .build();
        
            //broadcasterController.broadcastToSession(chatSession, responseMessage);
    
            LOGGER.info("Session {} reauthenticated with token {} of user {}",
                newSession.getId(), reauthenticateRequest.getToken(), user.getUsername()
            );
            
        } catch (ChatException e) {
            //broadcasterController.broadcastToSession(chatSession, errorMessage(e, MessageType.NOT_AUTHENTICATED));
            LOGGER.error("Failed to reauthenticate with token {}. Reason: {}",
                reauthenticateRequest.getToken(), e.getMessage()
            );
        }
        
    }
    
    @Override
    public void handleSignup(final SignupRequest signupRequest, final ChatSession chatSession) {
    
        try {
    
            validateSignUpRequest(signupRequest);
            
            userRepository
                .create(mapToUser(signupRequest))
                .subscribe(createdUser -> {
    
                    final AuthenticateRequest authenticateRequest = AuthenticateRequest.builder()
                        .username(signupRequest.getUsername())
                        .password(signupRequest.getPassword())
                        .build();
    
                    final ResponseMessage<Object> responseMessage = ResponseMessage
                        .builder()
                        .type(MessageType.SIGNUP)
                        .payload(authenticate(authenticateRequest, chatSession))
                        .build();
    
                    //broadcasterController.broadcastToSession(chatSession, responseMessage);
                    //chatMessageController.handleNewContact(createdUser, chatSession);
    
                    LOGGER.info("New user registered: {}", signupRequest.getUsername());
                    
                });
            
        } catch (ChatException e) {
            //broadcasterController.broadcastToSession(chatSession, errorMessage(e, MessageType.SIGNUP));
            LOGGER.error("Failed to create user {}. Reason: {}", signupRequest.getUsername(), e.getMessage());
        }
        
    }

    @Override
    public Optional<ChatSession> restoreSessionByToken(final ChatSession incompleteSession,
                                                       final String token) {
    
        return sessionRepository
            .tokenInUse(token)
            .blockOptional()
            .flatMap(existingSession -> {
    
                final String[] tokenData = new String(Base64.getDecoder().decode(token.getBytes(StandardCharsets.UTF_8)))
                    .split(TOKEN_SEPARATOR);
                
                return (tokenData.length == 3)
                    ? Optional.of(buildSessionFromToken(incompleteSession, token, tokenData))
                    : Optional.empty();
            });
    }
    
    @Override
    public void logoff(final ChatSession chatSession) {
        sessionRepository.logoff(chatSession);
    }
    
    private AuthenticateResponse authenticate(final AuthenticateRequest authenticateRequest, final ChatSession chatSession) {
        
        final User user = userRepository.findFullDetailsByUsername(authenticateRequest.getUsername())
            .blockOptional()
            .orElseThrow(() -> new ChatException("Invalid Credentials", INVALID_CREDENTIALS));
        
        if (user.getPassword().equals(authenticateRequest.getPassword())) {
            
            try {
    
                final ChatSession newSession = chatSession.from()
                    .id(UUID.randomUUID().toString())
                    .serverDetails(serverDetails)
                    .userDeviceDetails(authenticateRequest.getUserDeviceDetails())
                    .startDate(OffsetDateTime.now().toString())
                    .status(AUTHENTICATED)
                    .type(AUTHENTICATE)
                    .build();
    
                final String token = buildToken(newSession, user);
                sessionRepository.authenticate(newSession, user, token);
                
                LOGGER.info("New session authenticated: {}", newSession.getId());
                
                return AuthenticateResponse.builder()
                    .user(mapToUserDTO(user))
                    .token(token)
                    .status(ResponseStatus.SUCCESS)
                    .build();
                
            } catch (Exception e) {
                throw new ChatException("Failed to authenticate. Reason: " + e.getMessage(), ResponseStatus.SERVER_ERROR);
            }
            
        }
    
        throw new ChatException("Invalid Credentials", INVALID_CREDENTIALS);
    }
    
    private String buildToken(final ChatSession session, final User user) {
    
        final String token = session.getId() + TOKEN_SEPARATOR +
            user.getId() + TOKEN_SEPARATOR +
            session.getServerDetails().getServerInstanceId();
        
        return Base64
            .getEncoder()
            .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }
    
    private ChatSession buildSessionFromToken(final ChatSession incompleteSession,
                                              final String token,
                                              final String[] tokenData) {
        return incompleteSession.from()
            .id(tokenData[0])
            .userAuthenticationDetails(UserAuthenticationDetails.builder()
                .token(token)
                .userId(tokenData[1])
                .build()
            )
            .serverDetails(ServerDetails.builder()
                .serverInstanceId(tokenData[2])
                .build()
            )
            .type(AUTHENTICATE)
            .status(AUTHENTICATED)
            .build();
    }
    
    private void validateSignUpRequest(final SignupRequest signupRequest) {
        
        if (signupRequest.getName() == null || signupRequest.getName().trim().isEmpty()) {
            throw new ChatException("Name must be defined", INVALID_NAME);
        }
        
        if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty()) {
            throw new ChatException("Username must be defined", INVALID_USERNAME);
        }
        
        if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
            throw new ChatException("Username must be defined", INVALID_PASSWORD);
        }
        
    }
    
    private ResponseMessage<Object> errorMessage(final ChatException exception, final MessageType messageType) {
        return ResponseMessage
            .builder()
            .type(messageType)
            .payload(exception.toErrorMessage())
            .build();
        
    }
    
    private User mapToUser(final SignupRequest signupRequest) {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .username(signupRequest.getUsername())
            .password(signupRequest.getPassword())
            .name(signupRequest.getName())
            .avatar(avatarService.pickRandomAvatar())
            .description(DEFAULT_DESCRIPTION)
            .contactType(ContactType.USER)
            .createdDate(OffsetDateTime.now().toString())
            .build();
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