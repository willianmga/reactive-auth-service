package live.socialchat.auth.secret;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class BCryptHashingService implements HashingService {
    
    @Override
    public String hash(final String data) {
        return BCrypt.hashpw(data, BCrypt.gensalt());
    }
    
    @Override
    public boolean matches(final String data,
                           final String hash) {
        return BCrypt.checkpw(data, hash);
    }
    
}
