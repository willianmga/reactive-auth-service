package com.reactivechat.auth.authentication.legacy;

import com.reactivechat.auth.authentication.model.ChatSession;
import com.reactivechat.auth.signup.SignupRequest;

public interface AuthenticationController {
    void handleSignup(SignupRequest signupRequest, ChatSession chatSession);
}
