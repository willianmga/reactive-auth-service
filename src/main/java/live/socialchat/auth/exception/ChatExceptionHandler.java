package live.socialchat.auth.exception;

import live.socialchat.auth.authentication.model.RequestValidationErrorResponse;
import live.socialchat.auth.authentication.model.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ChatExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = RequestValidationException.class)
    public ResponseEntity<RequestValidationErrorResponse> handle(final RequestValidationException requestValidationException) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(RequestValidationErrorResponse.builder()
                .status(ResponseStatus.INVALID_REQUEST)
                .message(requestValidationException.getMessage())
                .errors(requestValidationException.getErrors())
                .build()
            );
    }

    @ExceptionHandler(value = ChatException.class)
    public ResponseEntity<ServerResponse> handle(final ChatException chatException) {
        return ResponseEntity
            .status(findHttpStatus(chatException.getResponseStatus()))
            .body(ServerResponse.builder()
                .status(chatException.getResponseStatus())
                .message(chatException.getMessage())
                .build()
            );
    }

    private HttpStatus findHttpStatus(final ResponseStatus responseStatus) {
        switch (responseStatus) {
            case INVALID_CREDENTIALS:
            case EXPIRED_TOKEN:
                return HttpStatus.UNAUTHORIZED;
            case SUCCESS:
                return HttpStatus.OK;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
    
}
