package live.socialchat.auth.exception;

import live.socialchat.auth.authentication.model.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ChatExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(value = RequestValidationException.class)
//    public Mono<ResponseEntity<ServerResponse>> handle(final RequestValidationException requestValidationException) {
//        return Mono.just(ResponseEntity
//            .status(HttpStatus.BAD_REQUEST)
//            .body(ServerResponse.builder()
//                .status(requestValidationException.getResponseStatus())
//                .message(requestValidationException.getMessage())
//                .build()
//            ));
//    }
//
//    @ExceptionHandler(value = ChatException.class)
//    public Mono<ResponseEntity<ServerResponse>> handle(final ChatException chatException) {
//        return Mono.just(ResponseEntity
//            .status(findHttpStatus(chatException.getResponseStatus()))
//            .body(ServerResponse.builder()
//                .status(chatException.getResponseStatus())
//                .message(chatException.getMessage())
//                .build()
//            ));
//    }

    private HttpStatus findHttpStatus(final ResponseStatus responseStatus) {
        switch (responseStatus) {
            case INVALID_CREDENTIALS:
            case EXPIRED_TOKEN:
                return HttpStatus.UNAUTHORIZED;
            case INVALID_NAME:
            case INVALID_PASSWORD:
            case INVALID_USERNAME:
                return HttpStatus.BAD_REQUEST;
            case SUCCESS:
                return HttpStatus.OK;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
    
}
