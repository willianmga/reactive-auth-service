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
                        //.payload(authenticate(authenticateRequest, chatSession))
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

}
