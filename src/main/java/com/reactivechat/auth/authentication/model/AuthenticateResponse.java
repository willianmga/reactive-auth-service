package com.reactivechat.auth.authentication.model;

import com.reactivechat.auth.exception.ResponseStatus;
import com.reactivechat.auth.user.model.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class AuthenticateResponse {
    
    private final String token;
    private final UserDTO user;
    private final ResponseStatus status;
    
}
