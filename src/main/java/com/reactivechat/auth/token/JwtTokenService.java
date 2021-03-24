package com.reactivechat.auth.token;

import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.exception.ChatException;
import com.reactivechat.auth.secret.SecretKeyService;
import com.reactivechat.auth.user.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.stereotype.Service;

import static com.reactivechat.auth.exception.ResponseStatus.EXPIRED_TOKEN;
import static com.reactivechat.auth.exception.ResponseStatus.INVALID_CREDENTIALS;

@Service
public class JwtTokenService implements TokenService {
    
    private static final String AUTH_SERVICE = "auth";
    private static final String SESSION_ID = "ses";
    private static final String ALGORITHM = "alg";
    private static final long TOKEN_DURATION_IN_MINUTES = 5;
    
    private final SecretKeyService secretKeyService;
    
    public JwtTokenService(final SecretKeyService secretKeyService) {
        this.secretKeyService = secretKeyService;
    }
    
    @Override
    public String create(final ChatSession session,
                         final User user) {
    
        return Jwts.builder()
            .setHeaderParam(ALGORITHM, SignatureAlgorithm.HS512.getJcaName())
            .setIssuer(AUTH_SERVICE)
            .setSubject(user.getId())
            .claim(SESSION_ID, session.getId())
            .setIssuedAt(Date.from(OffsetDateTime.now().toInstant()))
            .setExpiration(generateExpirationDate())
            .signWith(SignatureAlgorithm.HS512, secretKeyService.getJwtSecretKey())
            .compact();
    }
    
    @Override
    public Boolean validate(final String token) {
        
        try {
            
            Jwts.parserBuilder()
                .setSigningKey(secretKeyService.getJwtSecretKey())
                .build()
                .parseClaimsJws(token);
            
            return true;
        } catch (ExpiredJwtException e) {
            throw new ChatException("Token is expired", EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new ChatException("Token is invalid", INVALID_CREDENTIALS);
        }
    }
    
    private Date generateExpirationDate() {
        return Date.from(OffsetDateTime.now()
            .plusMinutes(TOKEN_DURATION_IN_MINUTES)
            .toInstant());
    }
    
}
