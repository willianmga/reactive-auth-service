package com.reactivechat.auth.avatar.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Avatar {
    
    private final String description;
    private final String source;
    private final String avatar;
    
    @JsonCreator
    public Avatar(@JsonProperty("description") final String description,
                  @JsonProperty("source") final String source,
                  @JsonProperty("avatar") final String avatar) {
        
        this.description = description;
        this.source = source;
        this.avatar = avatar;
    }
    
}
