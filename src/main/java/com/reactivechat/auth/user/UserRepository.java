package com.reactivechat.auth.user;

import com.reactivechat.auth.user.model.DestinationType;
import com.reactivechat.auth.user.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    
    Mono<User> create(final User user);
    Mono<User> findById(final String id);
    Mono<User> findFullDetailsByUsername(final String username);
    Flux<User> findContacts(final String userId);
    Mono<DestinationType> findDestinationType(final String destinationId);
    
}
