package com.nepbrain.technology.auth.config.security;

import com.nepbrain.technology.auth.config.GrantPasswordAuthenticationProvider;
import com.nepbrain.technology.auth.config.GrantSocialLoginAuthenticationProvider;
import com.nepbrain.technology.auth.config.JpaOAuth2AuthorizationService;
import com.nepbrain.technology.auth.config.OAuth2GrantPasswordAuthenticationConverter;
import com.nepbrain.technology.auth.config.OAuth2GrantSocialLoginAuthenticationConverter;
import com.nepbrain.technology.auth.exception.handler.OauthExceptionHandler;
import com.nepbrain.technology.auth.repository.RoleRepository;
import com.nepbrain.technology.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final OauthExceptionHandler oauthExceptionHandler;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationSecurityFilterChain(
            HttpSecurity http,
            GrantPasswordAuthenticationProvider grantPasswordAuthenticationProvider,
            GrantSocialLoginAuthenticationProvider grantSocialLoginAuthenticationProvider,
            DaoAuthenticationProvider daoAuthenticationProvider)
            throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

        authorizationServerConfigurer
                .tokenEndpoint(
                        tokenEndpoint ->
                                tokenEndpoint
                                        .accessTokenRequestConverter(new OAuth2GrantPasswordAuthenticationConverter())
                                        .accessTokenRequestConverter(new OAuth2GrantSocialLoginAuthenticationConverter())
                                        .authenticationProvider(grantPasswordAuthenticationProvider)
                                        .authenticationProvider(grantSocialLoginAuthenticationProvider)
                                        .authenticationProvider(daoAuthenticationProvider)
                                        .errorResponseHandler(oauthExceptionHandler));

        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);
        http.apply(authorizationServerConfigurer);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public GrantPasswordAuthenticationProvider grantPasswordAuthenticationProvider(
            UserDetailsService userDetailsService,
            OAuth2TokenGenerator<?> jwtTokenCustomizer,
            JpaOAuth2AuthorizationService authorizationService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RoleRepository roleRepository,
            HttpServletRequest httpServletRequest) {
        return new GrantPasswordAuthenticationProvider(
                authorizationService,
                jwtTokenCustomizer,
                userDetailsService,
                passwordEncoder,
                userRepository,
                roleRepository,
                httpServletRequest);
    }

    @Bean
    public GrantSocialLoginAuthenticationProvider grantSocialLoginAuthenticationProvider(
            OAuth2TokenGenerator<?> jwtTokenCustomizer,
            JpaOAuth2AuthorizationService authorizationService,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        return new GrantSocialLoginAuthenticationProvider(
                authorizationService, jwtTokenCustomizer, userRepository, roleRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
