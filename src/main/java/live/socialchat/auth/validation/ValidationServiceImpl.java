package live.socialchat.auth.validation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import live.socialchat.auth.exception.RequestValidationException;
import live.socialchat.auth.exception.RequestValidationException.ValidationError;
import live.socialchat.auth.exception.RequestValidationException.ValidationType;
import live.socialchat.auth.signup.model.SignupRequest;
import live.socialchat.auth.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import static live.socialchat.auth.exception.RequestValidationException.ValidationType.INVALID_EMAIL;
import static live.socialchat.auth.exception.RequestValidationException.ValidationType.INVALID_NAME;
import static live.socialchat.auth.exception.RequestValidationException.ValidationType.INVALID_PASSWORD;

@Service
public class ValidationServiceImpl implements ValidationService {
    
    private static final String NAME_VALIDATION_REGEX = "user.name.validation.regexp";
    private static final String EMAIL_VALIDATION_REGEX = "email.validation.regexp";
    private static final String USERNAME_VALIDATION_REGEX = "user.username.validation.regexp";
    private static final String PASSWORD_VALIDATION_REGEX = "user.password.validation.regexp";
    
    private final UserRepository userRepository;
    private final Pattern namePattern;
    private final Pattern emailPattern;
    private final Pattern usernamePattern;
    private final Pattern passwordPattern;
    
    @Autowired
    public ValidationServiceImpl(final Environment environment,
                                 final UserRepository userRepository) {
        this.userRepository = userRepository;
        this.namePattern = Pattern
            .compile(Objects.requireNonNull(environment.getProperty(NAME_VALIDATION_REGEX)));
        this.emailPattern = Pattern
            .compile(Objects.requireNonNull(environment.getProperty(EMAIL_VALIDATION_REGEX)));
        this.usernamePattern = Pattern
            .compile(Objects.requireNonNull(environment.getProperty(USERNAME_VALIDATION_REGEX)));
        this.passwordPattern = Pattern
            .compile(Objects.requireNonNull(environment.getProperty(PASSWORD_VALIDATION_REGEX)));
    }
    
    public void validateSignUpRequest(final SignupRequest signupRequest) {
        
        final Set<ValidationError> validationErrors = new HashSet<>();
        final boolean emailMatches = !emailPattern.matcher(signupRequest.getUsername()).find();
        final boolean usernameMatches = !usernamePattern.matcher(signupRequest.getUsername()).find();
        
        if (!namePattern.matcher(signupRequest.getName()).find()) {
            validationErrors.add(new ValidationError("Name does not meet the criteria", INVALID_NAME));
        }
        
        if (emailMatches) {
            validationErrors.add(new ValidationError("Email does not meet the criteria", INVALID_EMAIL));
        }
    
        if (usernameMatches) {
            validationErrors.add(new ValidationError("Username does not meet the criteria", INVALID_EMAIL));
        }
        
        if (!passwordPattern.matcher(signupRequest.getPassword()).find()) {
            validationErrors.add(new ValidationError("Password does not meet the criteria", INVALID_PASSWORD));
        }
        
        if (emailMatches && usernameMatches) {
            userRepository
                .findByUsernameOrEmail(signupRequest.getEmail(), signupRequest.getUsername())
                .toStream()
                .forEach((existingUser) -> {

                    if (existingUser.getUsername().equalsIgnoreCase(signupRequest.getUsername())) {
                        validationErrors.add(new ValidationError("Username already taken", ValidationType.USERNAME_IN_USE));
                    }
        
                    if (existingUser.getEmail().equalsIgnoreCase(signupRequest.getEmail())) {
                        validationErrors.add(new ValidationError("Email already taken", ValidationType.EMAIL_IN_USE));
                    }

                });
        }
        
        if (!validationErrors.isEmpty()) {
            throw new RequestValidationException(validationErrors);
        }

    }
    
}
