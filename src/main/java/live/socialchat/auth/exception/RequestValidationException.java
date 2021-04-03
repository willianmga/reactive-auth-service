package live.socialchat.auth.exception;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static live.socialchat.auth.exception.ResponseStatus.INVALID_REQUEST;

@Getter
public class RequestValidationException extends ChatException {

    private static final String MESSAGE = "Invalid request. Correct the errors and try again";
    
    private final List<ValidationError> errors;
    
    public RequestValidationException(final List<ValidationError> errors) {
        super(MESSAGE, INVALID_REQUEST);
        this.errors = Collections.unmodifiableList(errors);
    }
    
    public RequestValidationException(final ValidationError error) {
        super(MESSAGE, INVALID_REQUEST);
        this.errors = Collections.singletonList(error);
    }
    
    public RequestValidationException(final String message, final ValidationType type) {
        super(MESSAGE, INVALID_REQUEST);
        this.errors = Collections.singletonList(new ValidationError(message, type));
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
        INVALID_USERNAME,
        INVALID_PASSWORD,
        USERNAME_IN_USE,
    }
    
}
