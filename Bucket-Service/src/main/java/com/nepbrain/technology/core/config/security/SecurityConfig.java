package com.nepbrain.technology.core.config.security;

import com.nepbrain.technology.bucket.config.ServiceAuthenticationEntryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOpaqueTokenIntrospector customOpaqueTokenIntrospector;
    private final ServiceAuthenticationEntryConfig serviceAuthenticationEntryConfig;

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new OpaqueTokenReactiveAuthenticationManager(customOpaqueTokenIntrospector);
    }


    // Main security filter chain
    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        // Define public endpoints including Swagger
        String[] publicEndpoints = {
                "/",
                "/actuator",
                "/actuator/**",
                "/actuator/health",
                "/actuator/health/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs/**",
                "/v3/api-docs",
                "/swagger-resources/**",
                "/swagger-resources",
                "/webjars/**",
                "/webjars",
                "/favicon.ico",
                // Anonymous read of stored files (browser <img>, PDF links); uploads remain authenticated.
                "/api/v2/bucket/public/**",
        };

        http
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                    .opaqueToken(opaque -> opaque.introspector(customOpaqueTokenIntrospector))
                    .authenticationEntryPoint(serviceAuthenticationEntryConfig.authenticationEntryPoint()))
                .authorizeExchange(auth -> auth
                        .pathMatchers(publicEndpoints)
                        .permitAll()
                        // Same pattern as servlet services: Bearer must introspect successfully (opaque/JWT to auth server).
                        .anyExchange()
                        .authenticated());

        return http.build();
    }
}


