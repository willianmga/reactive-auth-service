package com.reactivechat.auth.token;

import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.user.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService implements TokenService {
    
    private static final String AUTH_SERVICE = "auth";
    private static final String SESSION_ID = "ses";
    private static final long TOKEN_DURATION_IN_MINUTES = 5;
    
    @Override
    public String create(final ChatSession session,
                         final User user) {
        return Jwts.builder()
            .setIssuer(AUTH_SERVICE)
            .setSubject(user.getId())
            .claim(SESSION_ID, session.getId())
            .setIssuedAt(Date.from(OffsetDateTime.now().toInstant()))
            .setExpiration(generateExpirationDate())
            .signWith(
                SignatureAlgorithm.HS256,
                "willianantoniodeazevedobodnariucwillianantoniodeazevedobodnariuc" // CHANGE to key
            )
            .compact();
    }
    
    private Date generateExpirationDate() {
        return Date.from(OffsetDateTime.now()
            .plusMinutes(TOKEN_DURATION_IN_MINUTES)
            .toInstant());
    }
    
}
