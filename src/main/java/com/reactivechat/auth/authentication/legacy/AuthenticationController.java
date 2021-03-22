package com.reactivechat.auth.authentication.legacy;

import com.reactivechat.auth.authentication.model.AuthenticateRequest;
import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.authentication.model.ReauthenticateRequest;
import com.reactivechat.auth.signup.SignupRequest;
import java.util.Optional;

public interface AuthenticationController {
    
    void handleAuthenticate(AuthenticateRequest authenticateRequest, ChatSession chatSession);
    void handleReauthenticate(ReauthenticateRequest reauthenticateRequest, ChatSession chatSession);
    void handleSignup(SignupRequest signupRequest, ChatSession chatSession);
    Optional<ChatSession> restoreSessionByToken(ChatSession incompleteSession, String token);
    void logoff(ChatSession chatSession);
    
}
