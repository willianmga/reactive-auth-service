package live.socialchat.auth.session;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.authentication.model.UserAuthenticationDetails;
import live.socialchat.auth.exception.ChatException;
import live.socialchat.auth.user.model.User;
import java.util.Collections;
import live.socialchat.auth.authentication.model.ChatSession.Status;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.set;

@Repository
public class MongoSessionRepository implements SessionRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSessionRepository.class);
    private static final String SESSIONS_COLLECTION = "user_session";
    private static final String SESSION_ID = "_id";
    private static final String USER_AUTHENTICATION_DETAILS = "userAuthenticationDetails";
    private static final String TOKEN = USER_AUTHENTICATION_DETAILS + ".token";
    private static final String SESSION_STATUS = "status";

    private static final Bson SERVER_SEARCH_FIELDS =
        fields(include("id"));
    
    private final MongoCollection<ChatSession> mongoCollection;
    
    @Autowired
    public MongoSessionRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(SESSIONS_COLLECTION, ChatSession.class);
    }

    @Override
    public void authenticate(final ChatSession chatSession, final User user, final String token) {
    
        tokenInUse(token)
            .blockOptional()
            .ifPresent((result) -> {
                throw new ChatException("Failed to authenticate session: Token is already in use by another session");
            });
    
        final UserAuthenticationDetails userAuthenticationDetails = UserAuthenticationDetails.builder()
            .userId(user.getId())
            .token(token)
            .build();
    
        final ChatSession newAuthenticatedSession = chatSession.from()
            .userAuthenticationDetails(userAuthenticationDetails)
            .build();
    
        Mono.from(mongoCollection.insertOne(newAuthenticatedSession))
            .doOnSuccess(result -> LOGGER.info("Inserted new session {}", result.getInsertedId()))
            .subscribe();
        
    }
    
    private ChatSession buildAuthenticatedSession(ChatSession chatSession,
                                                  User user,
                                                  String token) {
        return chatSession.from()
            .userAuthenticationDetails(UserAuthenticationDetails.builder()
                .userId(user.getId())
                .token(token)
                .build()
            )
            .build();
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

    private  Mono<ChatSession> tokenInUse(final String token) {
        return Mono.from(
            mongoCollection
                .find(and(
                    eq(TOKEN, token),
                    eq(SESSION_STATUS, Status.AUTHENTICATED.name()))
                )
                .projection(SERVER_SEARCH_FIELDS)
                .first()
        );
    }
    
}
