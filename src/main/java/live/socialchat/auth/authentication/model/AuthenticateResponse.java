package live.socialchat.auth.authentication.model;

import live.socialchat.auth.exception.ResponseStatus;
import live.socialchat.auth.user.model.UserDTO;
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
public class AuthenticateResponse {
    
    private final String token;
    private final UserDTO user;
    private final String tokenExpireDate;
    private final ResponseStatus status;
    
}
