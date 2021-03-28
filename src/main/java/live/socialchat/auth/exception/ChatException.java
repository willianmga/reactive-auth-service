package live.socialchat.auth.exception;

import live.socialchat.auth.authentication.model.ServerResponse;
import lombok.Getter;

import static live.socialchat.auth.exception.ResponseStatus.SERVER_ERROR;

@Getter
public class ChatException extends RuntimeException {
    
    private static final String SERVER_ERROR_MESSAGE = "A server error happened";
    
    private final ResponseStatus responseStatus;
    
    public ChatException(final String message) {
        this(message, SERVER_ERROR);
    }
    
    public ChatException(final String message, final ResponseStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }
    
    public ServerResponse toServerResponse() {
        
        final String errorMessage = (SERVER_ERROR.equals(responseStatus))
            ? SERVER_ERROR_MESSAGE
            : getMessage();
        
        return ServerResponse
            .builder()
            .status(responseStatus)
            .message(errorMessage)
            .build();
    }
    
}
