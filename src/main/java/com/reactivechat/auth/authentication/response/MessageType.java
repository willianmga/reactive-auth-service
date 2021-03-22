package com.reactivechat.auth.authentication.response;

import lombok.Getter;

@Getter
public enum MessageType {
    
    // Whitelisted messages. Does not require authentication to be received by server and sent to client

    AUTHENTICATE(true),
    REAUTHENTICATE(true),
    SIGNUP(true),
    NOT_AUTHENTICATED(true),
    NOT_AUTHORIZED(true),
    INVALID_REQUEST(true),
    
    // Blacklisted messages. Require authentication to be received by server and sent to client

    LOGOFF;

    MessageType() {
        this.whitelisted = false;
    }
    
    MessageType(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }
    
    private final boolean whitelisted;
    
}
