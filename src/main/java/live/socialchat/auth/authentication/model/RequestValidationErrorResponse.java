package live.socialchat.auth.authentication.model;

import java.util.Set;
import live.socialchat.auth.exception.RequestValidationException.ValidationError;
import live.socialchat.auth.exception.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class RequestValidationErrorResponse {
    
    private final String message;
    private final ResponseStatus status;
    private final Set<ValidationError> errors;

}
