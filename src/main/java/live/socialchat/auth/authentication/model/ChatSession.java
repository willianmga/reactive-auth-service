package live.socialchat.auth.authentication.model;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Builder
@ToString
public class ChatSession {
    
    @BsonId
    private final String id;
    private final ServerDetails serverDetails;
    private final UserDeviceDetails userDeviceDetails;
    private final UserAuthenticationDetails userAuthenticationDetails;
    private final String startDate;
    private final String expiryDate;
    private final Status status;
    
    @BsonCreator
    public ChatSession(@BsonProperty("id") String id,
                       @BsonProperty("serverDetails") ServerDetails serverDetails,
                       @BsonProperty("userDeviceDetails") UserDeviceDetails userDeviceDetails,
                       @BsonProperty("userAuthenticationDetails") UserAuthenticationDetails userAuthenticationDetails,
                       @BsonProperty("startDate") String startDate,
                       @BsonProperty("expiryDate") String expiryDate,
                       @BsonProperty("status") Status status) {
        this.id = id;
        this.serverDetails = serverDetails;
        this.userDeviceDetails = userDeviceDetails;
        this.userAuthenticationDetails = userAuthenticationDetails;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    @BsonIgnore
    public ChatSessionBuilder from() {
        return ChatSession.builder()
            .id(id)
            .userDeviceDetails(userDeviceDetails)
            .userAuthenticationDetails(userAuthenticationDetails)
            .serverDetails(serverDetails)
            .startDate(startDate)
            .expiryDate(expiryDate)
            .status(status);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatSession that = (ChatSession) o;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    public enum Status {
        AUTHENTICATED, LOGGED_OFF
    }
    
}
