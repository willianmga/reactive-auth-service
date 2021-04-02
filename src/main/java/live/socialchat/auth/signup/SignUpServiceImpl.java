package live.socialchat.auth.signup;

import java.time.OffsetDateTime;
import java.util.UUID;
import live.socialchat.auth.authentication.AuthenticationService;
import live.socialchat.auth.authentication.model.AuthenticateRequest;
import live.socialchat.auth.authentication.model.AuthenticateResponse;
import live.socialchat.auth.avatar.AvatarService;
import live.socialchat.auth.exception.ChatException;
import live.socialchat.auth.exception.RequestValidationException;
import live.socialchat.auth.exception.ResponseStatus;
import live.socialchat.auth.user.UserRepository;
import live.socialchat.auth.user.model.Contact.ContactType;
import live.socialchat.auth.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static live.socialchat.auth.exception.ResponseStatus.AUTHENTICATION_ERROR;
import static live.socialchat.auth.exception.ResponseStatus.SERVER_ERROR;

@Service
public class SignUpServiceImpl implements SignUpService {
    
    private static final String DEFAULT_DESCRIPTION = "Hi, I'm using SocialChat!";
    
    private final AuthenticationService authenticationService;
    private final AvatarService avatarService;
    private final UserRepository userRepository;
    
    @Autowired
    public SignUpServiceImpl(final AuthenticationService authenticationService,
                             final AvatarService avatarService,
                             final UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.avatarService = avatarService;
        this.userRepository = userRepository;
    }
    
    @Override
    public Mono<AuthenticateResponse> signup(final SignupRequest signupRequest) {
        return Mono.fromCallable(() -> validateSignUpRequest(signupRequest))
            .flatMap(response -> Mono.just(mapToUser(signupRequest)))
            .flatMap(userRepository::create)
            .switchIfEmpty(Mono.error(new ChatException("Failed to create User", SERVER_ERROR)))
            .flatMap(this::mapToAutenticateRequest)
            .flatMap(authenticationService::authenticate)
            .switchIfEmpty(Mono.error(new ChatException("Failed to authenticate User", AUTHENTICATION_ERROR)));
    }
    
    private void validateSignUpRequest(final SignupRequest signupRequest) {
        
        if (signupRequest.getName() == null || signupRequest.getName().trim().isEmpty()) {
            throw new RequestValidationException("Name must be defined", ResponseStatus.INVALID_NAME);
        }
        
        if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty()) {
            throw new RequestValidationException("Username must be defined", ResponseStatus.INVALID_USERNAME);
        }
        
        if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
            throw new RequestValidationException("Username must be defined", ResponseStatus.INVALID_PASSWORD);
        }
        
    }
    
    private Mono<AuthenticateRequest> mapToAutenticateRequest(final User user) {
        return Mono.just(AuthenticateRequest.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .build());
    }
    
    private User mapToUser(final SignupRequest signupRequest) {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .username(signupRequest.getUsername().toLowerCase())
            .password(signupRequest.getPassword())
            .name(signupRequest.getName())
            .avatar(avatarService.pickRandomAvatar())
            .description(DEFAULT_DESCRIPTION)
            .contactType(ContactType.USER)
            .createdDate(OffsetDateTime.now().toString())
            .build();
    }
    
}