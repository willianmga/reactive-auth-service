package live.socialchat.auth.exception;

public enum ResponseStatus {
    SUCCESS,
    INVALID_REQUEST,
    AUTHENTICATION_ERROR,
    INVALID_CREDENTIALS,
    EXPIRED_TOKEN,
    SERVER_ERROR
}
