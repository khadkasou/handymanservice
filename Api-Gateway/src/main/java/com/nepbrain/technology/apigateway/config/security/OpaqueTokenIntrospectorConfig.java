package com.nepbrain.technology.apigateway.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Custom opaque token introspector that avoids Reactor Netty for introspection calls.
 * This bypasses "Expect: 100-continue" behavior that can trigger 417 on some auth servers.
 */
@Configuration
public class OpaqueTokenIntrospectorConfig {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri}")
    private String introspectionUri;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret}")
    private String clientSecret;

    @Bean
    public ReactiveOpaqueTokenIntrospector reactiveOpaqueTokenIntrospector() {
        SpringOpaqueTokenIntrospector delegate = SpringOpaqueTokenIntrospector
                .withIntrospectionUri(introspectionUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        return token -> Mono.fromCallable(() -> delegate.introspect(token))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(
                        OAuth2IntrospectionException.class,
                        ex -> new BadOpaqueTokenException("Token introspection failed", ex));
    }
}
