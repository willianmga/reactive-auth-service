package live.socialchat.auth.secret;

import javax.crypto.SecretKey;

public interface SecretKeyService {
    SecretKey getJwtSecretKey();
}
