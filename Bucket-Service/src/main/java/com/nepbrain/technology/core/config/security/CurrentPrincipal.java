package com.nepbrain.technology.core.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Helper class to get OAuth2AuthenticatedPrincipal without using @AuthenticationPrincipal annotation.
 * Provides methods for both blocking and reactive contexts.
 */
@Slf4j
public class CurrentPrincipal {

    /**
     * Blocking method to get current OAuth2AuthenticatedPrincipal.
     * This method works in both blocking and reactive contexts.
     * It first tries SecurityContextHolder (blocking), then falls back to reactive context.
     * 
     * @return OAuth2AuthenticatedPrincipal or null if not found
     */
    public static OAuth2AuthenticatedPrincipal getPrincipal() {
        try {
            // First, try blocking SecurityContextHolder
            Authentication authentication = null;
            try {
                authentication = SecurityContextHolder.getContext().getAuthentication();
                log.info("🔍 SecurityContextHolder authentication: {}", authentication != null ? authentication.getClass().getSimpleName() : "null");
            } catch (Exception e) {
                log.debug("SecurityContextHolder not available, trying reactive context: {}", e.getMessage());
            }

            // Process authentication if we have it from blocking context
            if (authentication != null && authentication.isAuthenticated()) {
                // Try to get from BearerTokenAuthentication (opaque token)
                if (authentication instanceof BearerTokenAuthentication bearerAuth) {
                    Object principalObj = bearerAuth.getPrincipal();
                    if (principalObj instanceof OAuth2AuthenticatedPrincipal principal) {
                        log.info("✅ Extracted principal from blocking context");
                        return principal;
                    } else {
                        log.warn("⚠️ Principal is not OAuth2AuthenticatedPrincipal. Type: {}", 
                                principalObj != null ? principalObj.getClass().getSimpleName() : "null");
                    }
                } else {
                    log.warn("⚠️ Authentication is not BearerTokenAuthentication. Type: {}", authentication.getClass().getSimpleName());
                }
            }

            // If blocking context didn't work, try reactive context
            log.info("🔍 Trying reactive context...");
            try {
                // Use publishOn to switch to bounded elastic scheduler before blocking
                OAuth2AuthenticatedPrincipal reactivePrincipal = getPrincipalReactive()
                        .doOnNext(p -> log.info("🔍 Reactive principal extracted: {}", p != null ? "present" : "null"))
                        .publishOn(Schedulers.boundedElastic())
                        .blockOptional(java.time.Duration.ofSeconds(5))
                        .orElse(null);
                if (reactivePrincipal != null) {
                    log.info("✅ Extracted principal from reactive context");
                    return reactivePrincipal;
                } else {
                    log.warn("⚠️ Reactive principal is null");
                }
            } catch (IllegalStateException e) {
                // Handle "block()/blockFirst()/blockLast() are blocking" error
                if (e.getMessage() != null && e.getMessage().contains("blocking")) {
                    log.error("⚠️ Cannot block on reactive context. Error: {}", e.getMessage());
                } else {
                    log.error("⚠️ IllegalStateException: {}", e.getMessage(), e);
                }
            } catch (Exception e) {
                log.error("⚠️ Error getting reactive principal: {}", e.getMessage(), e);
            }

            log.warn("⚠️ Could not extract principal from any context. Authentication: {}", authentication);
            return null;
        } catch (IllegalStateException e) {
            // Handle "block()/blockFirst()/blockLast() are blocking" error
            if (e.getMessage() != null && e.getMessage().contains("blocking")) {
                log.error("⚠️ Cannot block on reactive context. Use getPrincipalReactive() in reactive chains.");
            }
            return null;
        } catch (Exception e) {
            log.error("⚠️ Error extracting principal: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Reactive method to get current OAuth2AuthenticatedPrincipal from opaque token.
     * This method is non-blocking and works in reactive Spring Boot applications.
     * 
     * @return Mono containing the OAuth2AuthenticatedPrincipal or empty Mono if not found
     */
    public static Mono<OAuth2AuthenticatedPrincipal> getPrincipalReactive() {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(ctx -> log.info("🔍 ReactiveSecurityContextHolder context: {}", ctx != null ? "present" : "null"))
                .cast(SecurityContext.class)
                .map(SecurityContext::getAuthentication)
                .doOnNext(auth -> {
                    log.info("🔍 Found authentication: {}, authenticated: {}", auth.getClass().getSimpleName(), auth.isAuthenticated());
                })
                .filter(authentication -> authentication.isAuthenticated())
                .doOnNext(auth -> log.info("🔍 Authentication passed filter"))
                .cast(BearerTokenAuthentication.class)
                .doOnNext(bearerAuth -> log.info("🔍 BearerTokenAuthentication cast successful"))
                .map(BearerTokenAuthentication::getPrincipal)
                .doOnNext(principal -> log.info("🔍 Principal type: {}, Principal: {}",
                        principal.getClass().getSimpleName(), principal))
                .cast(OAuth2AuthenticatedPrincipal.class)
                .doOnNext(principal -> log.info("✅ Extracted principal from reactive context. Attributes: {}", principal.getAttributes()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("⚠️ No principal found in opaque token");
                    return Mono.empty();
                }))
                .doOnError(error -> log.error("⚠️ Error extracting principal from reactive security context: {}", error.getMessage(), error));
    }

    /**
     * Blocking method to get current OAuth2AuthenticatedPrincipal with Optional return type.
     * 
     * @return Optional containing the OAuth2AuthenticatedPrincipal or empty if not found
     */
    public static java.util.Optional<OAuth2AuthenticatedPrincipal> getPrincipalOptional() {
        OAuth2AuthenticatedPrincipal principal = getPrincipal();
        return principal != null ? java.util.Optional.of(principal) : java.util.Optional.empty();
    }
}

