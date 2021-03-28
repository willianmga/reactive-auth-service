package live.socialchat.auth.user.model;

public interface Contact {
    
    String getId();
    String getName();
    String getAvatar();
    String getDescription();
    ContactType getContactType();
    
    enum ContactType {
        USER,
        GROUP
    }
    
}
