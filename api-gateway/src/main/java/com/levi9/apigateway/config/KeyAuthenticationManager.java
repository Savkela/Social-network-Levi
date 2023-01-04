package com.levi9.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class KeyAuthenticationManager implements ReactiveAuthenticationManager {
    private static final Logger LOG = LoggerFactory.getLogger(KeyAuthenticationManager.class);

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.fromSupplier(() -> {
            if (authentication != null && authentication.getCredentials() != null) {
                authentication.setAuthenticated(true);
            }

            return authentication;
        });
    }
}
