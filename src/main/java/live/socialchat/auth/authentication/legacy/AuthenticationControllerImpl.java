package live.socialchat.auth.authentication.legacy;

import live.socialchat.auth.authentication.model.AuthenticateRequest;
import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.authentication.model.ServerDetails;
import live.socialchat.auth.authentication.response.MessageType;
import live.socialchat.auth.authentication.response.ResponseMessage;
import live.socialchat.auth.avatar.AvatarService;
import live.socialchat.auth.avatar.AvatarServiceImpl;
import live.socialchat.auth.exception.ChatException;
import live.socialchat.auth.exception.ResponseStatus;
import live.socialchat.auth.session.SessionRepository;
import live.socialchat.auth.signup.SignupRequest;
import live.socialchat.auth.user.UserRepository;
import live.socialchat.auth.user.model.Contact.ContactType;
import live.socialchat.auth.user.model.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
            throw new ChatException("Name must be defined", ResponseStatus.INVALID_NAME);
        }
        
        if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty()) {
            throw new ChatException("Username must be defined", ResponseStatus.INVALID_USERNAME);
        }
        
        if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
            throw new ChatException("Username must be defined", ResponseStatus.INVALID_PASSWORD);
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
