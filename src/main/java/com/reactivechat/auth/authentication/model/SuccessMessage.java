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
public class SuccessMessage {
    
    private final String message;
    private final ResponseStatus status;
    
    public static SuccessMessage create(final String message) {
        return SuccessMessage.builder()
            .message(message)
            .status(ResponseStatus.SUCCESS)
            .build();
    }
    
}
