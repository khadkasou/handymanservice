package com.nepbrain.technology.core.config.security;

import com.nepbrain.technology.core.exception.CustomAccessDeniedHandler;
import com.nepbrain.technology.core.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomOpaqueTokenIntrospector customOpaqueTokenIntrospector;

    // Separate filter chain for Swagger endpoints (runs first, no OAuth2)
    @Bean
    @Order(1)
    SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs",
                "/swagger-resources/**",
                "/webjars/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    // Main security filter chain for all other endpoints
    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Define public endpoints
        String[] publicEndpoints = {
            "/",
            "/actuator/**",
            "/actuator/health/**",
            "/api/v2/auth/register",
            "/api/v2/auth/login",
            "/api/v2/auth/social-login",
            "/api/v2/auth/forgot-password",
            "/api/v2/config/configurations",
            "/api/v2/config/firebase-detail",
            "/api/v2/geo/**",
            "/api/v2/catalog/category-list",
            "/api/v2/catalog/subcategory-list",
            "/api/v2/catalog/service-list",
            "/api/v2/catalog/service-detail",
            "/api/v2/catalog/search-list"
        };

        http.authorizeHttpRequests(auth -> auth.requestMatchers(publicEndpoints).permitAll().anyRequest()
                .authenticated())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        // Configure OAuth2 resource server for authenticated endpoints
        http.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaque ->
                opaque.introspector(customOpaqueTokenIntrospector))
                .authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }
}
