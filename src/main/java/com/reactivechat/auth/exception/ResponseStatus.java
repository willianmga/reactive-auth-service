package com.reactivechat.auth.exception;

public enum ResponseStatus {
    SUCCESS,
    INVALID_CREDENTIALS,
    EXPIRED_TOKEN,
    INVALID_NAME,
    INVALID_USERNAME,
    INVALID_PASSWORD,
    USERNAME_IN_USE,
    SERVER_ERROR
}
