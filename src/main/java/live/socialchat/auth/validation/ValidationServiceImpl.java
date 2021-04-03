package live.socialchat.auth.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import live.socialchat.auth.exception.RequestValidationException;
import live.socialchat.auth.exception.RequestValidationException.ValidationError;
import live.socialchat.auth.signup.model.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import static live.socialchat.auth.exception.RequestValidationException.ValidationType.INVALID_NAME;
import static live.socialchat.auth.exception.RequestValidationException.ValidationType.INVALID_PASSWORD;
import static live.socialchat.auth.exception.RequestValidationException.ValidationType.INVALID_USERNAME;

@Service
public class ValidationServiceImpl implements ValidationService {
    
    private static final String NAME_VALIDATION_REGEX = "user.name.validation.regexp";
    private static final String USERNAME_VALIDATION_REGEX = "user.username.validation.regexp";
    private static final String PASSWORD_VALIDATION_REGEX = "user.password.validation.regexp";
    
    private final Pattern namePattern;
    private final Pattern usernamePattern;
    private final Pattern passwordPattern;
    
    @Autowired
    public ValidationServiceImpl(final Environment environment) {
        this.namePattern = Pattern
            .compile(Objects.requireNonNull(environment.getProperty(NAME_VALIDATION_REGEX)));
        this.usernamePattern = Pattern
            .compile(Objects.requireNonNull(environment.getProperty(USERNAME_VALIDATION_REGEX)));
        this.passwordPattern = Pattern
            .compile(Objects.requireNonNull(environment.getProperty(PASSWORD_VALIDATION_REGEX)));
    }
    
    public void validateSignUpRequest(final SignupRequest signupRequest) {
        
        final List<ValidationError> validationErrors = new ArrayList<>();

        if (!usernamePattern.matcher(signupRequest.getUsername()).find()) {
            validationErrors.add(new ValidationError("Username does not meet the criteria", INVALID_USERNAME));
        }
        
        if (!passwordPattern.matcher(signupRequest.getPassword()).find()) {
            validationErrors.add(new ValidationError("Password does not meet the criteria", INVALID_PASSWORD));
        }
    
        if (!namePattern.matcher(signupRequest.getName()).find()) {
            validationErrors.add(new ValidationError("Name does not meet the criteria", INVALID_NAME));
        }
        
        if (!validationErrors.isEmpty()) {
            throw new RequestValidationException(validationErrors);
        }
        
    }
    
}
