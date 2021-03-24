package com.reactivechat.auth.token;

import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenService {
    String create(ChatSession session, User user);
    Jws<Claims> validate(String token);
}
