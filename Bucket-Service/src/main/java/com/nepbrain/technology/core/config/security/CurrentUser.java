package com.nepbrain.technology.core.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
public class CurrentUser {

    /**
     * Blocking method to get current user ID.
     * Works in blocking contexts (traditional Spring MVC).
     * 
     * @return UUID of the current user or null if not found
     */
    public static UUID getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            return UUID.fromString(userId);
        }
        return null;
    }

    /**
     * Blocking method to get current user ID as String from opaque token.
     * This method works in blocking contexts and extracts userId from opaque token.
     * 
     * @return userId as String or null if not found
     */
    public static String getUserId() {
        String userId = resolveUserIdFromReactiveContext();
        if (userId != null) {
            return userId;
        }
        return resolveUserIdFromSecurityContextHolder();
    }

    private static String resolveUserIdFromReactiveContext() {
        try {
            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .map(CurrentUser::extractUserId)
                    .filter(id -> id != null && !id.isBlank())
                    .block();
        } catch (Exception e) {
            log.debug("Could not read userId from reactive security context: {}", e.getMessage());
            return null;
        }
    }

    private static String resolveUserIdFromSecurityContextHolder() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("No authenticated user found in security context");
                return null;
            }
            String userId = extractUserId(authentication);
            if (userId == null) {
                log.warn("Could not extract userId from authentication");
            }
            return userId;
        } catch (Exception e) {
            log.error("Error extracting userId: {}", e.getMessage(), e);
            return null;
        }
    }

    private static String extractUserId(Authentication authentication) {
        if (authentication instanceof BearerTokenAuthentication bearerAuth) {
            Object principalObj = bearerAuth.getPrincipal();
            if (principalObj instanceof OAuth2AuthenticatedPrincipal principal) {
                Object userIdAttr = principal.getAttribute("userId");
                if (userIdAttr != null && !userIdAttr.toString().isEmpty()) {
                    return userIdAttr.toString();
                }
                Object subAttr = principal.getAttribute("sub");
                if (subAttr != null && !subAttr.toString().isEmpty()) {
                    return subAttr.toString();
                }
                String principalName = principal.getName();
                if (principalName != null && !principalName.isEmpty()
                        && !principalName.equalsIgnoreCase("anonymousUser")) {
                    return principalName;
                }
            }
        }

        String userId = authentication.getName();
        if (userId != null && !userId.isEmpty() && !userId.equalsIgnoreCase("anonymousUser")) {
            return userId;
        }
        return null;
    }

    /**
     * Reactive method to get current user ID from opaque token.
     * This method is non-blocking and works in reactive Spring Boot applications.
     * Extracts userId from opaque token introspection response (sub claim).
     * 
     * @return Mono containing the userId (sub claim) as String or empty Mono if not found
     */
    public static Mono<String> getUserIdReactive() {
        return ReactiveSecurityContextHolder.getContext()
                .cast(SecurityContext.class)
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication != null && authentication.isAuthenticated())
                .cast(BearerTokenAuthentication.class)
                .map(BearerTokenAuthentication::getPrincipal)
                .cast(OAuth2AuthenticatedPrincipal.class)
                .map(principal -> {
                    // Try userId attribute first (set by CustomOpaqueTokenIntrospector)
                    Object userIdAttr = principal.getAttribute("userId");
                    if (userIdAttr != null && !userIdAttr.toString().isEmpty()) {
                        return userIdAttr.toString();
                    }
                    // Try sub attribute (from introspection response)
                    Object subAttr = principal.getAttribute("sub");
                    if (subAttr != null && !subAttr.toString().isEmpty()) {
                        return subAttr.toString();
                    }
                    // Try principal name as fallback
                    String principalName = principal.getName();
                    if (principalName != null && !principalName.isEmpty() && !principalName.equalsIgnoreCase("anonymousUser")) {
                        return principalName;
                    }
                    return null;
                })
                .filter(userId -> userId != null && !userId.isEmpty() && !userId.equalsIgnoreCase("anonymousUser"))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("⚠️ No userId found in opaque token principal attributes");
                    return Mono.empty();
                }))
                .doOnNext(userId -> log.debug("✅ Extracted userId from opaque token: {}", userId))
                .doOnError(error -> log.error("⚠️ Error extracting userId from reactive security context: {}", error.getMessage(), error));
    }

    /**
     * Reactive method to get current user ID as UUID.
     * This method is non-blocking and works in reactive Spring Boot applications.
     * 
     * @return Mono containing the userId as UUID or empty Mono if not found
     */
    public static Mono<UUID> getUserAsUuid() {
        return getUserIdReactive()
                .map(UUID::fromString)
                .doOnError(error -> {
                    if (error instanceof IllegalArgumentException) {
                        log.error("⚠️ Invalid UUID format for userId: {}", error.getMessage());
                    }
                });
    }

    /**
     * Blocking method to get current user ID as UUID from opaque token.
     * 
     * @return UUID of the current user or null if not found
     */
    public static UUID getUserAsUuidBlocking() {
        String userId = getUserId();
        if (userId != null) {
            try {
                return UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                log.error("⚠️ Invalid UUID format for userId: {}", userId);
                return null;
            }
        }
        return null;
    }
}
