package live.socialchat.auth.authentication.model;

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
public class ServerResponse {
    
    private final String message;
    private final ResponseStatus status;
    
    public static ServerResponse success(final String message) {
        return ServerResponse.builder()
            .message(message)
            .status(ResponseStatus.SUCCESS)
            .build();
    }
    
}
