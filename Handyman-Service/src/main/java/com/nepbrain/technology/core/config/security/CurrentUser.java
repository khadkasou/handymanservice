package com.nepbrain.technology.core.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;

import java.util.UUID;

public class CurrentUser {

    /** Get user ID as UUID (for backward compatibility) */
    public static UUID getUser() {
        String userId = getUserIdAsString();
        if (userId != null) {
            try {
                return UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                // User ID is not a valid UUID format
                return null;
            }
        }
        return null;
    }

    /** Get user ID as String from opaque token This method works with OAuth2 opaque tokens */
    public static String getUserIdAsString() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equalsIgnoreCase("anonymousUser")) {
            return null;
        }

        // For OAuth2 opaque tokens, the principal is OAuth2AuthenticatedPrincipal
        if (auth.getPrincipal() instanceof OAuth2AuthenticatedPrincipal) {
            OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) auth.getPrincipal();
            // The user ID is set as the principal's name by CustomOpaqueTokenIntrospector
            String userId = principal.getName();

            // Alternative: Get from attributes if name is not set
            if (userId == null || userId.isEmpty()) {
                userId = (String) principal.getAttribute(OAuth2TokenIntrospectionClaimNames.USERNAME);
            }

            return userId;
        }

        // Fallback to authentication name for other authentication types
        return auth.getName();
    }
}
