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

/**
 * Servlet-based security (no WebFlux) to avoid duplicate conversionServicePostProcessor
 * with WebFluxSecurityConfiguration. permitAll for all requests, no OAuth2 resource server.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    @Order(1)
    SecurityFilterChain swaggerAndActuatorFilterChain(HttpSecurity http) throws Exception {
        String[] publicPaths = {
                "/",
                "/actuator",
                "/actuator/**",
                "/actuator/health",
                "/actuator/health/**",
                "/api/v2/communication/public/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs/**",
                "/v3/api-docs",
                "/swagger-resources/**",
                "/swagger-resources",
                "/webjars/**",
                "/webjars",
                "/favicon.ico"
        };
        http.securityMatcher(publicPaths)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
