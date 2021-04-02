package live.socialchat.auth.session;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import java.util.Collections;
import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.authentication.model.ChatSession.Status;
import live.socialchat.auth.authentication.model.UserAuthenticationDetails;
import live.socialchat.auth.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

@Repository
public class MongoSessionRepository implements SessionRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSessionRepository.class);
    private static final String SESSIONS_COLLECTION = "user_session";
    private static final String SESSION_ID = "_id";
    private static final String SESSION_STATUS = "status";
    
    private final MongoCollection<ChatSession> mongoCollection;
    
    @Autowired
    public MongoSessionRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(SESSIONS_COLLECTION, ChatSession.class);
    }

    @Override
    public void authenticate(final ChatSession chatSession, final User user, final String token) {
        
        Mono.just(buildAuthenticatedSession(chatSession, user, token))
            .flatMap(newChatSession -> Mono.from(mongoCollection.insertOne(newChatSession)))
            .doOnError(error -> LOGGER.error("Failed to insert session to db {}. Reason: {}", chatSession.getId(), error.getMessage()))
            .doOnSuccess(result -> LOGGER.info("Inserted new session {}", result.getInsertedId()))
            .subscribe();
        
    }

    @Override
    public Mono<Boolean> logoff(final String sessionId) {
        return Mono.from(
                mongoCollection.updateMany(
                    eq(SESSION_ID, sessionId),
                    Collections.singletonList(
                        set(SESSION_STATUS, Status.LOGGED_OFF.name())
                    )
                )
            )
            .doOnError(error -> LOGGER.error("Failed to inactivate session {}. Reason: {}", sessionId, error.getMessage()))
            .doOnSuccess(result -> LOGGER.info("Session {} inactivated", sessionId))
            .flatMap(updateResult -> Mono.just(updateResult.getModifiedCount() > 0));
    }

    private ChatSession buildAuthenticatedSession(final ChatSession chatSession, final User user, final String token) {
        return chatSession.from()
            .userAuthenticationDetails(UserAuthenticationDetails.builder()
                .userId(user.getId())
                .token(token)
                .build()
            )
            .build();
    }
    
}
