package com.nepbrain.technology.apigateway.config.security;

import com.nepbrain.technology.apigateway.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveOpaqueTokenIntrospector reactiveOpaqueTokenIntrospector;

    public SecurityConfig(ReactiveOpaqueTokenIntrospector reactiveOpaqueTokenIntrospector) {
        this.reactiveOpaqueTokenIntrospector = reactiveOpaqueTokenIntrospector;
    }

    public String[] whitelistEndpoints() {
        return new String[] {
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/v3/api-docs/bucket-service",
                "/v3/api-docs/communication-service",
                "/webjars/**",
                "/configuration/ui",
                "/configuration/security",
                "/login",
                "/actuator/**",
                "/oauth2/**",
                "/api/v2/public/**",
                "/api/v2/communication/public/**",
                "/api/v2/master/public/**",
                "/api/v2/user/public/**",
                "/api/v2/user/login",
                "/api/v2/user/forgot/password",
                "/api/v2/user/reset-password/otp",
                "/api/v2/user/registration",
                "/api/v2/user/update/password",
                "/api/v2/user/resend/otp",
                "/api/v2/user/refresh/token",
                "/api/v2/public/user/mobile/refresh-token",
                "/api/v2/public/user/mobile/refresh/token",
                "/api/v2/user/email-verification",
                "/api/v2/public/company/resolve-tenant",
                "/",
                "/api/v2/bucket/public/retrieve-bucket/**",
                "/api/v2/bucket/retrieve/**", "/api/v2/bucket/document/retrieve/**"};
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(whitelistEndpoints()).permitAll()

                        .pathMatchers("/api/v2/**").authenticated()
                        .anyExchange().permitAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaqueToken -> opaqueToken
                                .introspector(reactiveOpaqueTokenIntrospector))
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        return http.build();
    }


}
