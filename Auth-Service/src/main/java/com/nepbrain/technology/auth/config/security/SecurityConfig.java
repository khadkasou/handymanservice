package com.nepbrain.technology.auth.config.security;

import com.nepbrain.technology.auth.exception.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Value("${oauth.introspectUri}")
    private String introspectUri;

    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.secret}")
    private String secret;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain oauth2ClientSecurityFilterChain(
            HttpSecurity http,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) throws Exception {
        return http
                .securityMatcher("/oauth2/**", "/login/oauth2/**", "/auth/social/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Authentication failed\",\"message\":\"" + exception.getMessage() + "\"}");
                        }))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http, OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter)
            throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/actuator/health",
                                "/oauth2/**", "/login/oauth2/**", "/auth/social/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                        )
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2
                                        .opaqueToken(
                                                opaque ->
                                                        opaque
                                                                .introspectionUri(introspectUri)
                                                                .introspectionClientCredentials(clientId, secret)
                                                                .authenticationConverter(opaqueTokenAuthenticationConverter))
                                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(LogoutConfigurer::permitAll)
                .build();
    }

    @Bean
    public OpaqueTokenAuthenticationConverter customOpaqueTokenAuthenticationConverter() {
        return new CustomOpaqueTokenAuthenticationConverter();
    }

}
