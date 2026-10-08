package com.nepbrain.technology.core.config.security;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.TokenIntrospectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    @Value(
            "${spring.security.oauth2.resourceserver.opaque-token.introspection-uri:http://localhost:8080/oauth2/introspect}")
    private String authServerUrl;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id:swift}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret:secret}")
    private String clientSecret;

    private final UserRepository userRepository;
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        log.debug("Introspecting opaque token: {}", token);

        try {
            TokenIntrospectionResponse response = introspectToken(token);

            if (response != null && response.isActive()) {
                return createPrincipal(response);
            }
        } catch (Exception e) {
            log.debug("OAuth introspection did not accept the token: {}", e.getMessage());
        }
        return apiTokenPrincipal(token);
    }

    /**
     * Customer login issues a UUID apiToken stored on the Handyman user. Accept that token when
     * Auth-Service has no matching OAuth token.
     */
    private OAuth2AuthenticatedPrincipal apiTokenPrincipal(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return userRepository.findByApiToken(token)
                .filter(user -> user.getStatus() == null || user.getStatus() == StatusEnum.ACTIVE)
                .map(this::principalForUser)
                .orElse(null);
    }

    private OAuth2AuthenticatedPrincipal principalForUser(UserEntity user) {
        String userId = user.getId() == null ? "" : user.getId().toString();
        return new OAuth2AuthenticatedPrincipal() {
            @Override
            public Map<String, Object> getAttributes() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put(OAuth2TokenIntrospectionClaimNames.ACTIVE, true);
                attributes.put(OAuth2TokenIntrospectionClaimNames.USERNAME, userId);
                attributes.put("sub", userId);
                attributes.put("email", user.getEmail());
                return attributes;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                if (user.getUserType() != null) {
                    authorities.add(new SimpleGrantedAuthority(user.getUserType().name()));
                }
                return authorities;
            }

            @Override
            public String getName() {
                return userId;
            }
        };
    }

    private TokenIntrospectionResponse introspectToken(String token) {
        String introspectionUrl = authServerUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        String body = "token=" + token;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<TokenIntrospectionResponse> response = restTemplate.postForEntity(
                introspectionUrl, request, TokenIntrospectionResponse.class);

        TokenIntrospectionResponse responseBody = response.getBody();
        log.debug("Introspection response: {}", responseBody);

        return responseBody;
    }

    private OAuth2AuthenticatedPrincipal createPrincipal(TokenIntrospectionResponse response) {
        log.debug("Creating principal for response: active={}, clientId={}, userId={}, scopes={},"
                + " authorities={}, issuedAt={}, expiresAt={}",
                response.isActive(),
                response.getClientId(),
                response.getUserId(),
                response.getScopes(),
                response.getAuthorities(),
                response.getIssuedAt(),
                response.getExpiresAt());

        return new OAuth2AuthenticatedPrincipal() {
            @Override
            public Map<String, Object> getAttributes() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put(OAuth2TokenIntrospectionClaimNames.ACTIVE, response.isActive());
                attributes.put(OAuth2TokenIntrospectionClaimNames.CLIENT_ID,
                        response.getClientId() != null ? response.getClientId() : "");

                // Store user ID from sub or username
                String userId = response.getUserId();
                if (userId != null && !userId.isEmpty()) {
                    attributes.put(OAuth2TokenIntrospectionClaimNames.USERNAME, userId);
                    attributes.put("sub", userId); // Also store as sub for OAuth2 standard
                } else {
                    attributes.put(OAuth2TokenIntrospectionClaimNames.USERNAME, "");
                }

                attributes.put(OAuth2TokenIntrospectionClaimNames.SCOPE,
                        response.getScopes() != null ? response.getScopes() : "");
                if (response.getAuthorities() != null) {
                    attributes.put("authorities", response.getAuthorities());
                }
                if (response.getIssuedAt() != null) {
                    attributes.put("iat", response.getIssuedAt());
                }
                if (response.getExpiresAt() != null) {
                    attributes.put("exp", response.getExpiresAt());
                }
                return attributes;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                Collection<GrantedAuthority> authorities = new ArrayList<>();

                // Add scopes as authorities
                if (response.getScopes() != null && !response.getScopes().isEmpty()) {
                    Arrays.stream(response.getScopes().split("\\s+")).forEach(scope -> {
                            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                            authorities.add(new SimpleGrantedAuthority(scope));
                    });
                }

                // Add user permissions/authorities
                if (response.getAuthorities() != null && !response.getAuthorities().isEmpty()) {
                    response.getAuthorities().stream().filter(auth -> auth != null && !auth.trim().isEmpty()).forEach(
                            auth -> authorities.add(new SimpleGrantedAuthority(auth.trim())));
                }

                return authorities;
            }

            @Override
            public String getName() {
                return response.getUserId();
            }
        };
    }
}
