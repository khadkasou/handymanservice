package com.nepbrain.technology.core.config;

import com.nepbrain.technology.core.service.AesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Global WebClient configuration:
 * - Adds AES-encrypted XSRF token header
 * - Propagates request ID across downstream calls
 * - Automatically handles Bearer token for secured microservice calls
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebClientConfig {

    private static final String HEADER_XSRF = "X-Xsrf-Token";
    private static final String HEADER_REQUEST_ID = "Request-Id";

    private final AesService aesService;

    @Value("${aes.encryption.xsrfToken}")
    private String xsrfToken;

    @Value("${server.url:}")
    private String baseUrl;

    // =====================================================
    // WebClient.Builder — required by communication HTTP clients (CustomerCommunication, etc.)
    // =====================================================
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader(HEADER_XSRF, encryptToken(xsrfToken))
                .filter(addRequestIdPropagation())
                .filter(new ServerBearerExchangeFilterFunction())
                .filter(loggingFilter());
    }

    // =====================================================
    // Pre-built WebClient (optional base URL from server.url)
    // =====================================================
    @Bean
    public WebClient webClient() {
        String encryptedXsrf = encryptToken(xsrfToken);

        return WebClient.builder()
                .baseUrl(Optional.ofNullable(baseUrl).filter(s -> !s.isBlank()).orElse(null))
                .defaultHeader(HEADER_XSRF, encryptedXsrf)
                .filter(addRequestIdPropagation())
                .filter(new ServerBearerExchangeFilterFunction()) // handles OAuth2 Bearer token
                .filter(loggingFilter()) // optional lightweight request/response logging
                .build();
    }

    // =====================================================
    // Request ID propagation filter
    // =====================================================
    private ExchangeFilterFunction addRequestIdPropagation() {
        return (clientRequest, next) -> Mono.deferContextual(context -> {
            ServerWebExchange exchange = context.get(ServerWebExchange.class);
            if (exchange != null) {
                String requestId = Optional.ofNullable(
                        exchange.getRequest().getHeaders().getFirst(HEADER_REQUEST_ID)
                ).orElse("");
                ClientRequest mutatedRequest = ClientRequest.from(clientRequest)
                        .header(HEADER_REQUEST_ID, requestId)
                        .build();
                return next.exchange(mutatedRequest);
            }
            return next.exchange(clientRequest);
        });
    }

    // =====================================================
    // Lightweight logging filter (for debug visibility)
    // =====================================================
    private ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.debug("🌐 [WebClient] Request -> {} {}", request.method(), request.url());
            return Mono.just(request);
        });
    }

    // =====================================================
    // Token encryption helper
    // =====================================================
    private String encryptToken(String token) {
        try {
            return aesService.encrypt(token);
        } catch (Exception e) {
            log.error("❌ Failed to encrypt XSRF token: {}", e.getMessage());
            return "invalid-xsrf-token";
        }
    }
}
