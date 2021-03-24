package com.reactivechat.auth.token;

import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.user.model.User;

public interface TokenService {
    String create(ChatSession session, User user);
    Boolean validate(String token);
}
