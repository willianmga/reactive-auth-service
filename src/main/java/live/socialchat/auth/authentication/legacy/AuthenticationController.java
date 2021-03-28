package live.socialchat.auth.authentication.legacy;

import live.socialchat.auth.authentication.model.ChatSession;
import live.socialchat.auth.signup.SignupRequest;

public interface AuthenticationController {
    void handleSignup(SignupRequest signupRequest, ChatSession chatSession);
}
