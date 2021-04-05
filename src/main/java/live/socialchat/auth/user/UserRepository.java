package live.socialchat.auth.user;

import live.socialchat.auth.user.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> create(User user);
    Mono<User> findFullDetailsByUsername(String username);
    Flux<User> findByUsernameOrEmail(String email, String username);
}
