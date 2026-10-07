package com.nepbrain.technology.auth.config;

import com.nepbrain.technology.auth.entity.ClientEntity;
import com.nepbrain.technology.auth.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Seeds the OAuth2 registered client in the Auth-Service database.
 * User-Service login/refresh calls Auth-Service /oauth2/token with these credentials.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${oauth.clientId}")
    private String oauthClientId;

    @Value("${oauth.secret}")
    private String oauthClientSecret;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initializeOAuthClient();
    }

    private void initializeOAuthClient() {
        try {
            seedOrUpdateOAuthClient();
        } catch (Exception e) {
            log.error(
                    "OAuth2 client init failed (is table 'client' missing?). "
                            + "For local, set JPA_DDL_AUTO=update once, or create the client table. Cause: {}",
                    e.getMessage());
            throw e;
        }
    }

    private static final String DEFAULT_GRANT_TYPES =
            "refresh_token,client_credentials,authorization_code,custom_password,social_login";

    private void seedOrUpdateOAuthClient() {
        var existing = clientRepository.findByClientId(oauthClientId);
        if (existing.isPresent()) {
            ClientEntity client = existing.get();
            boolean changed = false;
            if (!passwordEncoder.matches(oauthClientSecret, client.getClientSecret())) {
                log.warn(
                        "OAuth2 client '{}' secret in DB does not match oauth.secret / AUTH_CLIENT_SECRET; updating hash.",
                        oauthClientId);
                client.setClientSecret(passwordEncoder.encode(oauthClientSecret));
                changed = true;
            }
            String grants = client.getAuthorizationGrantTypes() != null
                    ? client.getAuthorizationGrantTypes()
                    : "";
            if (!grants.contains("social_login")) {
                client.setAuthorizationGrantTypes(DEFAULT_GRANT_TYPES);
                changed = true;
                log.info("OAuth2 client '{}' grant types updated to include social_login.", oauthClientId);
            }
            if (changed) {
                clientRepository.save(client);
            } else {
                log.info("OAuth2 client '{}' already exists in Auth-Service, skipping initialization.", oauthClientId);
            }
            return;
        }

        log.info("Initializing OAuth2 client '{}' in Auth-Service...", oauthClientId);

        ClientEntity client = ClientEntity.builder()
                .id(UUID.randomUUID().toString())
                .clientId(oauthClientId)
                .clientIdIssuedAt(Instant.now())
                .clientSecret(passwordEncoder.encode(oauthClientSecret))
                .clientSecretExpiresAt(null)
                .clientName("abbc70f1-fb59-4b42-b1e4-c52fa0080bea")
                .clientAuthenticationMethods("client_secret_basic")
                .authorizationGrantTypes(DEFAULT_GRANT_TYPES)
                .redirectUris("http://127.0.0.1:8080/login/oauth2/code/" + oauthClientId)
                .postLogoutRedirectUris("")
                .scopes("read,profile")
                .clientSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":true,\"settings.client.require-authorization-consent\":true}")
                .tokenSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",60.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"reference\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000]}")
                .build();

        clientRepository.save(client);
        log.info("OAuth2 client '{}' initialized successfully in Auth-Service.", oauthClientId);
    }
}
