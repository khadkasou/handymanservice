package com.nepbrain.technology.core.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class ServiceAuthenticationEntryConfig {
    
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            // Manually construct JSON to ensure data field is not included
            String jsonResponse = "{\"status\":\"FAILED\",\"code\":\"401\",\"message\":\"Unauthorized: Invalid or expired token\"}";

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        };
    }
}
