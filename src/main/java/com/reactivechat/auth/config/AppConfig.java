package com.reactivechat.auth.config;

import com.reactivechat.auth.authentication.model.ServerDetails;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Bean
    public ServerDetails serverDetails() {
        return ServerDetails.builder()
            .serverInstanceId(UUID.randomUUID().toString())
            .build();
    }
    
}
