package com.reactivechat.auth.secret;

import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SecretKeyService {
    
    private static final String JWT_SECRET_KEY = "JWT_SECRET_KEY";
    
    private final SecretKey jwtSecretKey;
    
    public SecretKeyService(final Environment environment) {
        this.jwtSecretKey = generate(environment, JWT_SECRET_KEY);
    }
    
    public SecretKey getJwtSecretKey() {
        return jwtSecretKey;
    }
    
    private static SecretKey generate(final Environment environment, final String keyName) {
        final String jwtSecretKey = environment.getProperty(keyName);
        Objects.requireNonNull(jwtSecretKey, "Error: couldn't find secret key");
        final byte[] decodedKey = Base64.getDecoder().decode(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, SignatureAlgorithm.HS512.getJcaName());
    }
    
}
