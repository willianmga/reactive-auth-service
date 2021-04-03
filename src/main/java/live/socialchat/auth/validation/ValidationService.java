package live.socialchat.auth.validation;

import live.socialchat.auth.signup.model.SignupRequest;

public interface ValidationService {
    void validateSignUpRequest(SignupRequest signupRequest);
}
