package live.socialchat.auth.token;

import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.exception.ChatException;
import live.socialchat.auth.secret.SecretKeyService;
import live.socialchat.auth.token.model.CreateTokenResponse;
import live.socialchat.auth.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.OffsetDateTime;
import java.util.Date;
import live.socialchat.auth.exception.ResponseStatus;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService implements TokenService {
    
    public static final String SESSION_ID = "ses";
    private static final String AUTH_SERVICE = "auth";
    private static final String ALGORITHM = "alg";
    private static final long TOKEN_DURATION_IN_HOURS = 8;
    
    private final SecretKeyService secretKeyService;
    
    public JwtTokenService(final SecretKeyService secretKeyService) {
        this.secretKeyService = secretKeyService;
    }
    
    @Override
    public CreateTokenResponse create(final ChatSession session,
                                      final User user) {
    
        final OffsetDateTime tokenExpireDate = OffsetDateTime.now()
            .plusHours(TOKEN_DURATION_IN_HOURS);
        
        final String token = Jwts.builder()
            .setHeaderParam(ALGORITHM, SignatureAlgorithm.HS512.getJcaName())
            .setIssuer(AUTH_SERVICE)
            .setSubject(user.getId())
            .claim(SESSION_ID, session.getId())
            .setIssuedAt(Date.from(OffsetDateTime.now().toInstant()))
            .setExpiration(Date.from(tokenExpireDate.toInstant()))
            .signWith(secretKeyService.getJwtSecretKey())
            .compact();
        
        return CreateTokenResponse.builder()
            .token(token)
            .tokenExpireDate(tokenExpireDate)
            .build();
    }
    
    @Override
    public Jws<Claims> validate(final String token) {
        
        try {
            
            return Jwts.parserBuilder()
                .setSigningKey(secretKeyService.getJwtSecretKey())
                .build()
                .parseClaimsJws(token);
            
        } catch (ExpiredJwtException e) {
            throw new ChatException("Token is expired", ResponseStatus.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new ChatException("Token is invalid", ResponseStatus.INVALID_CREDENTIALS);
        }
    }
    
    @Override
    public Jws<Claims> parse(final String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKeyService.getJwtSecretKey())
            .build()
            .parseClaimsJws(token);
    }
    
}
