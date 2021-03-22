package com.reactivechat.auth.authentication.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ResponseMessage<T> {

    private final MessageType type;
    private final T payload;
    
}