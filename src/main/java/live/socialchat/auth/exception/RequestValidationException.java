package live.socialchat.auth.exception;

public class RequestValidationException extends ChatException {
    
    public RequestValidationException(String message) {
        super(message);
    }
    
    public RequestValidationException(String message,
                                      ResponseStatus responseStatus) {
        super(message, responseStatus);
    }
    
}
