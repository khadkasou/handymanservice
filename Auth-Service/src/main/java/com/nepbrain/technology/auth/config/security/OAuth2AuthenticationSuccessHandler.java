package com.nepbrain.technology.auth.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.auth.config.JpaOAuth2AuthorizationService;
import com.nepbrain.technology.auth.config.JpaRegisteredClientRepository;
import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.auth.service.CustomUserDetail;
import com.nepbrain.technology.auth.service.RoleAuthoritySupport;
import com.nepbrain.technology.auth.service.SocialAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SocialAuthService socialAuthService;
    private final UserRepository userRepository;
    private final JpaOAuth2AuthorizationService authorizationService;
    private final JpaRegisteredClientRepository registeredClientRepository;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final ObjectMapper objectMapper;

    @Value("${oauth.clientId}")
    private String defaultClientId;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String provider = oauth2Token.getAuthorizedClientRegistrationId();

            // Process OAuth2 user and get UserDetails
            UserDetails userDetails = socialAuthService.processOAuth2User(oauth2User, provider);

            // Get the user entity by ID (username is the user ID as string)
            UserEntity userEntity = userRepository.findById(
                    java.util.UUID.fromString(userDetails.getUsername())).orElseThrow(
                    () -> new RuntimeException("User not found after OAuth2 authentication"));

            // Create CustomUserDetail to get authorities
            CustomUserDetail customUserDetail = new CustomUserDetail(
                    userEntity,
                    userDetails.getUsername(),
                    RoleAuthoritySupport.authoritiesFromRoles(userEntity.getRoles()));

            // Generate token response
            Map<String, Object> tokenResponse = generateTokenResponse(customUserDetail, userEntity);

            // Write JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
            response.getWriter().flush();
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private Map<String, Object> generateTokenResponse(CustomUserDetail customUserDetail, UserEntity userEntity) {
        try {
            // Get registered client
            RegisteredClient registeredClient = registeredClientRepository.findByClientId(defaultClientId);
            if (registeredClient == null) {
                throw new RuntimeException("Registered client not found");
            }

            // Create authentication token
            User springUser = new User(
                    String.valueOf(userEntity.getId()),
                    userEntity.getPassword(),
                    userEntity.isEnabled(),
                    userEntity.isAccountNonExpired(),
                    userEntity.isCredentialsNonExpired(),
                    userEntity.isAccountNonLocked(),
                    customUserDetail.getAuthorities()
            );

            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    springUser, null, springUser.getAuthorities());

            // Generate access token
            DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                    .registeredClient(registeredClient)
                    .principal(authToken)
                    .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                    .authorizedScopes(registeredClient.getScopes())
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);

            OAuth2TokenContext tokenContext = tokenContextBuilder
                    .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                    .build();

            OAuth2Token generatedAccessToken = tokenGenerator.generate(tokenContext);
            if (generatedAccessToken == null) {
                throw new RuntimeException("Failed to generate access token");
            }

            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    generatedAccessToken.getTokenValue(),
                    generatedAccessToken.getIssuedAt(),
                    generatedAccessToken.getExpiresAt(),
                    tokenContext.getAuthorizedScopes());

            // Generate refresh token
            OAuth2RefreshToken refreshToken = null;
            if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
                tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
                OAuth2Token generatedRefreshToken = tokenGenerator.generate(tokenContext);
                if (generatedRefreshToken instanceof OAuth2RefreshToken) {
                    refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
                }
            }

            // Save authorization
            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                    .id(String.valueOf(userEntity.getId()))
                    .principalName(String.valueOf(userEntity.getId()))
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizedScopes(registeredClient.getScopes())
                    .accessToken(accessToken);

            if (refreshToken != null) {
                authorizationBuilder.refreshToken(refreshToken);
            }

            authorizationService.save(authorizationBuilder.build());

            // Build response
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("access_token", accessToken.getTokenValue());
            response.put("token_type", "Bearer");
            response.put("expires_in", accessToken.getExpiresAt() != null ?
                    accessToken.getExpiresAt().getEpochSecond() - System.currentTimeMillis() / 1000 : 3600);
            if (refreshToken != null) {
                response.put("refresh_token", refreshToken.getTokenValue());
            }
            response.put("user", Map.of(
                    "id", userEntity.getId().toString(),
                    "email", userEntity.getEmailAddress() != null ? userEntity.getEmailAddress() : "",
                    "provider", userEntity.getAuthProvider() != null ? userEntity.getAuthProvider() : ""
            ));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token response", e);
        }
    }
}

