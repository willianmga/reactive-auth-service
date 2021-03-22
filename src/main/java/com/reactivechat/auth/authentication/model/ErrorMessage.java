package com.reactivechat.auth.authentication.model;

import com.reactivechat.auth.exception.ResponseStatus;
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
public class ErrorMessage {
    
    private final String message;
    private final ResponseStatus status;
    
}
