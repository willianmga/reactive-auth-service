package live.socialchat.auth.exception;

import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {
    
    private final ResponseStatus responseStatus;
    
    public ChatException(final String message, final ResponseStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }

}
