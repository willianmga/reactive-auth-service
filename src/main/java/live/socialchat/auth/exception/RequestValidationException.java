package live.socialchat.auth.exception;

import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static live.socialchat.auth.exception.ResponseStatus.INVALID_REQUEST;

@Getter
public class RequestValidationException extends ChatException {

    private static final String MESSAGE = "Invalid request. Correct the errors and try again";
    
    private final Set<ValidationError> errors;
    
    public RequestValidationException(final Set<ValidationError> errors) {
        super(MESSAGE, INVALID_REQUEST);
        this.errors = Collections.unmodifiableSet(errors);
    }
    
    public RequestValidationException(final ValidationError error) {
        super(MESSAGE, INVALID_REQUEST);
        this.errors = Collections.singleton(error);
    }
    
    public RequestValidationException(final String message, final ValidationType type) {
        super(MESSAGE, INVALID_REQUEST);
        this.errors = Collections.singleton(new ValidationError(message, type));
    }
    
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ValidationError {
        private final String message;
        private final ValidationType type;
    }
    
    public enum ValidationType {
        INVALID_NAME,
        INVALID_EMAIL,
        INVALID_PASSWORD,
        USERNAME_IN_USE,
        EMAIL_IN_USE,
    }
    
}
