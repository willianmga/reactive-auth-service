package live.socialchat.auth.avatar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.socialchat.auth.avatar.model.Avatar;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AvatarServiceImpl implements AvatarService {
    
    private static final List<Avatar> AVAILABLE_AVATARS = loadAvailableAvatars();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    @Override
    public String pickRandomAvatar() {
        final int avatarIndex = SECURE_RANDOM.nextInt(AVAILABLE_AVATARS.size());
        return AVAILABLE_AVATARS.get(avatarIndex).getAvatar();
    }
    
    private static List<Avatar> loadAvailableAvatars() {
        try {
            URL resource = AvatarServiceImpl.class.getClassLoader().getResource("user-avatars.json");
            TypeReference<List<Avatar>> typeReference = new TypeReference<List<Avatar>>() {};
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource, typeReference);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read available avatars");
        }
    }
    
}
