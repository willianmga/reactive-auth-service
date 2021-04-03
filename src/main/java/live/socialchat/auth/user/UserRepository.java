package live.socialchat.auth.user;

import live.socialchat.auth.user.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> create(final User user);
    Mono<User> findFullDetailsByUsername(final String username);
}
