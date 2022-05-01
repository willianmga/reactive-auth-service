package live.socialchat.auth.healthcheck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HealthCheckResponse {
    private final String message;
}
