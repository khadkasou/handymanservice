package com.nepbrain.technology.core.config.security;

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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri:http://localhost:8080/oauth2/introspect}")
    private String authServerUrl;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id:swift}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret:secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        log.debug("Introspecting opaque token: {}", token);
        
        return Mono.fromCallable(() -> {
            try {
                TokenIntrospectionResponse response = introspectToken(token);
                
                if (!response.isActive()) {
                    log.debug("Token is not active");
                    throw new OAuth2AuthenticationException(
                        new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Token is not active", null)
                    );
                }
                
                return createPrincipal(response);
                
            } catch (OAuth2AuthenticationException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error introspecting token: {}", e.getMessage(), e);
                throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Token introspection failed: " + e.getMessage(), null),
                    e
                );
            }
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    private TokenIntrospectionResponse introspectToken(String token) {
        String introspectionUrl = authServerUrl;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        // Form-encode the token (JWTs contain + / =); raw "token=" + token breaks introspection.
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<TokenIntrospectionResponse> response = restTemplate.postForEntity(
                    introspectionUrl, request, TokenIntrospectionResponse.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Introspection endpoint returned non-2xx status: {}", response.getStatusCode());
                throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Token introspection failed with status: " + response.getStatusCode(), null)
                );
            }
            
            TokenIntrospectionResponse responseBody = response.getBody();
            if (responseBody == null) {
                log.warn("Introspection endpoint returned null response body");
                throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Token introspection returned empty response", null)
                );
            }
            
            log.debug("Introspection response: {}", responseBody);
            
            return responseBody;
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (RestClientException e) {
            if (e instanceof HttpStatusCodeException hs && hs.getStatusCode().value() == 401) {
                log.error(
                    "Introspection POST returned 401 from {} — auth server rejected the confidential client credentials "
                        + "(not the user token). Set spring.security.oauth2.resourceserver.opaque-token.client-id / client-secret "
                        + "(AUTH_CLIENT_ID and AUTH_CLIENT_SECRET, or AUTH_SECRET as alias) to match an OAuth2 client allowed to call introspection — "
                        + "use the same values as Employee-Service / gateway resource servers. configured_client_id={}",
                    introspectionUrl,
                    clientId
                );
            } else {
                log.error("Error calling introspection endpoint: {}", e.getMessage(), e);
            }
            throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Failed to introspect token: " + e.getMessage(), null),
                e
            );
        }
    }

    private OAuth2AuthenticatedPrincipal createPrincipal(TokenIntrospectionResponse response) {
        // Convert Long timestamps to Instant if needed
        Instant issuedAt = response.getIssuedAt();
        if (issuedAt == null && response.getIat() != null) {
            issuedAt = Instant.ofEpochSecond(response.getIat());
        }
        
        Instant expiresAt = response.getExpiresAt();
        if (expiresAt == null && response.getExp() != null) {
            expiresAt = Instant.ofEpochSecond(response.getExp());
        }
        
        // Make final copies for use in inner class
        final Instant finalIssuedAt = issuedAt;
        final Instant finalExpiresAt = expiresAt;
        final Long iatValue = response.getIat();
        final Long expValue = response.getExp();
        
        // Extract values with proper fallbacks
        final String username = response.getUsername() != null && !response.getUsername().isEmpty() 
                ? response.getUsername() 
                : (response.getUserId() != null ? response.getUserId() : "");
        final String clientId = response.getClientId() != null && !response.getClientId().isEmpty() 
                ? response.getClientId() 
                : "";
        final String scope = response.getScopes() != null && !response.getScopes().isEmpty() 
                ? response.getScopes() 
                : "";
        final String userId = response.getUserId() != null && !response.getUserId().isEmpty() 
                ? response.getUserId() 
                : username;
        
        log.debug("Creating principal for response: active={}, clientId={}, username={}, userId={}, scopes={}, authorities={}, issuedAt={}, expiresAt={}", 
                response.isActive(), clientId, username, userId, scope, 
                response.getAuthorities(), finalIssuedAt, finalExpiresAt);
        
        return new OAuth2AuthenticatedPrincipal() {
            @Override
            public Map<String, Object> getAttributes() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put(OAuth2TokenIntrospectionClaimNames.ACTIVE, response.isActive());
                attributes.put(OAuth2TokenIntrospectionClaimNames.CLIENT_ID, clientId);
                attributes.put(OAuth2TokenIntrospectionClaimNames.USERNAME, username);
                attributes.put("userId", userId);
                attributes.put("sub", userId);
                attributes.put(OAuth2TokenIntrospectionClaimNames.SCOPE, scope);
                if (response.getAuthorities() != null) {
                    attributes.put("authorities", response.getAuthorities());
                }
                if (finalIssuedAt != null) {
                    attributes.put("iat", finalIssuedAt);
                } else if (iatValue != null) {
                    attributes.put("iat", iatValue);
                }
                if (finalExpiresAt != null) {
                    attributes.put("exp", finalExpiresAt);
                } else if (expValue != null) {
                    attributes.put("exp", expValue);
                }
                return attributes;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                
                // Add scopes as authorities
                if (scope != null && !scope.isEmpty()) {
                    Arrays.stream(scope.split("\\s+"))
                            .forEach(scopeValue -> {
                                authorities.add(new SimpleGrantedAuthority("SCOPE_" + scopeValue));
                                authorities.add(new SimpleGrantedAuthority(scopeValue));
                            });
                }
                
                // Add user permissions/authorities
                if (response.getAuthorities() != null && !response.getAuthorities().isEmpty()) {
                    response.getAuthorities().stream()
                            .filter(auth -> auth != null && !auth.trim().isEmpty())
                            .forEach(auth -> authorities.add(new SimpleGrantedAuthority(auth.trim())));
                }
                
                return authorities;
            }

            @Override
            public String getName() {
                return username;
            }
        };
    }
}
