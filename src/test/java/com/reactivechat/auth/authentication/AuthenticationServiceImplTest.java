package com.reactivechat.auth.authentication;

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;

public class AuthenticationServiceImplTest {
    
    @Test
    public void tesT() throws InterruptedException {
    
        Mono.create( sink -> {
    
    
            sink.success(createError("first"));
            
        } )
            .onErrorResume(error -> {
                System.out.println("handler 1: an error occured: " + error.getMessage());
                return Mono.just("error");
            })
            .flatMap(result -> Mono.just(createError("second")))
//            .onErrorResume(error -> {
//                System.out.println("handler 2: an error occured: " + error.getMessage());
//                return Mono.just("error");
//            })
            .doOnSuccess(result -> System.out.println("finished"))
            .onErrorResume(error -> {
                System.out.println("handler 1: an error occured: " + error.getMessage());
                return Mono.just("error");
            })
            .subscribe(result -> {
                System.out.println("here is result: " + result);
            });
     
        Thread.sleep(5000);
        
    }
    
    private String createError(String message) {
        throw new IllegalArgumentException(message);
    }
    
    
}