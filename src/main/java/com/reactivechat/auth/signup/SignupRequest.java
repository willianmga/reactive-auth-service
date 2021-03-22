package com.reactivechat.auth.signup;

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
public class SignupRequest {
    
    private final String username;
    private final String password;
    private final String name;
    
}
