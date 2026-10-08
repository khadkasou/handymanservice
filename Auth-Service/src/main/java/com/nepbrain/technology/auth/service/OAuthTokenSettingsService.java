package com.nepbrain.technology.auth.service;

import com.nepbrain.technology.auth.config.JpaRegisteredClientRepository;
import com.nepbrain.technology.auth.exception.ServiceException;
import com.nepbrain.technology.auth.payload.request.OAuthTokenSettingsRequest;
import com.nepbrain.technology.auth.payload.response.OAuthTokenSettingsResponse;
import com.nepbrain.technology.auth.payload.response.ServiceResponse;
import com.nepbrain.technology.auth.payload.response.ServiceResponseBuilder;
import com.nepbrain.technology.auth.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OAuthTokenSettingsService {

    private static final long MIN_ACCESS_TTL_SECONDS = 60L;
    /** Allow multi-day access tokens (was capped at 24h). */
    private static final long MAX_ACCESS_TTL_SECONDS = 315_360_000L; // 10 years
    private static final long MIN_REFRESH_TTL_SECONDS = 300L; // 5m
    private static final long MAX_REFRESH_TTL_SECONDS = 315_360_000L; // 10 years

    private final ClientRepository clientRepository;
    private final JpaRegisteredClientRepository registeredClientRepository;

    @Value("${oauth.clientId}")
    private String oauthClientId;

    @Transactional(readOnly = true)
    public ServiceResponse getCurrent() {
        RegisteredClient client = requireClient();
        return ServiceResponseBuilder.buildSuccess("message.oauth.settings.loaded.success", toResponse(client));
    }

    @Transactional
    public ServiceResponse update(OAuthTokenSettingsRequest request) {
        if (request == null) {
            throw new ServiceException("OTS001", "Request body is required", HttpStatus.BAD_REQUEST);
        }
        long accessSeconds = requirePositive(
                request.getAccessTokenTtlSeconds(), "accessTokenTtlSeconds",
                MIN_ACCESS_TTL_SECONDS, MAX_ACCESS_TTL_SECONDS);
        long refreshSeconds = requirePositive(
                request.getRefreshTokenTtlSeconds(), "refreshTokenTtlSeconds",
                MIN_REFRESH_TTL_SECONDS, MAX_REFRESH_TTL_SECONDS);
        if (refreshSeconds < accessSeconds) {
            throw new ServiceException(
                    "OTS002",
                    "Refresh token TTL must be greater than or equal to access token TTL",
                    HttpStatus.BAD_REQUEST);
        }

        RegisteredClient existing = requireClient();
        TokenSettings current = existing.getTokenSettings();
        TokenSettings.Builder tokenBuilder = TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofSeconds(accessSeconds))
                .refreshTokenTimeToLive(Duration.ofSeconds(refreshSeconds))
                .reuseRefreshTokens(current.isReuseRefreshTokens())
                .accessTokenFormat(current.getAccessTokenFormat())
                .authorizationCodeTimeToLive(current.getAuthorizationCodeTimeToLive());

        RegisteredClient updated = RegisteredClient.from(existing)
                .tokenSettings(tokenBuilder.build())
                .build();
        registeredClientRepository.save(updated);
        return ServiceResponseBuilder.buildSuccess("message.oauth.settings.updated.success", toResponse(requireClient()));
    }

    private RegisteredClient requireClient() {
        return clientRepository.findByClientId(oauthClientId)
                .map(registeredClientRepository::toObject)
                .orElseThrow(() -> new ServiceException(
                        "OTS003",
                        "OAuth client not found: " + oauthClientId,
                        HttpStatus.NOT_FOUND));
    }

    private static long requirePositive(Long value, String field, long min, long max) {
        if (value == null) {
            throw new ServiceException("OTS001", field + " is required", HttpStatus.BAD_REQUEST);
        }
        if (value < min || value > max) {
            throw new ServiceException(
                    "OTS001",
                    field + " must be between " + min + " and " + max + " seconds",
                    HttpStatus.BAD_REQUEST);
        }
        return value;
    }

    private static OAuthTokenSettingsResponse toResponse(RegisteredClient client) {
        Duration access = client.getTokenSettings().getAccessTokenTimeToLive();
        Duration refresh = client.getTokenSettings().getRefreshTokenTimeToLive();
        long accessSeconds = access != null ? access.getSeconds() : 0L;
        long refreshSeconds = refresh != null ? refresh.getSeconds() : 0L;
        return OAuthTokenSettingsResponse.builder()
                .clientId(client.getClientId())
                .accessTokenTtlSeconds(accessSeconds)
                .refreshTokenTtlSeconds(refreshSeconds)
                .accessTokenTtlLabel(formatDuration(accessSeconds))
                .refreshTokenTtlLabel(formatDuration(refreshSeconds))
                .build();
    }

    private static String formatDuration(long totalSeconds) {
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }
        long days = totalSeconds / 86_400;
        long hours = (totalSeconds % 86_400) / 3_600;
        long minutes = (totalSeconds % 3_600) / 60;
        long seconds = totalSeconds % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0 || sb.isEmpty()) {
            sb.append(seconds).append("s");
        }
        return sb.toString().trim();
    }
}
