package live.socialchat.auth.token.model;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateTokenResponse {
    
    private final String token;
    private final OffsetDateTime tokenExpireDate;
    
}
