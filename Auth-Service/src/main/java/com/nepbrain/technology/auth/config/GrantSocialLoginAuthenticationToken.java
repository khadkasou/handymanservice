package com.nepbrain.technology.auth.config;

import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.nepbrain.technology.auth.config.AuthorizationGrantTypeSocialLogin.GRANT_SOCIAL_LOGIN;

/**
 * Confidential-client grant that issues tokens for an already-verified social user (username = email).
 * Password is not required — User-Service verifies the provider token first.
 */
@Getter
public class GrantSocialLoginAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String username;
    private final Set<String> scopes;

    public GrantSocialLoginAuthenticationToken(
            Authentication clientPrincipal,
            String username,
            @Nullable Set<String> scopes,
            @Nullable Map<String, Object> additionalParameters) {
        super(GRANT_SOCIAL_LOGIN, clientPrincipal, additionalParameters);
        Assert.hasText(username, "username cannot be empty");
        this.username = username;
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrantSocialLoginAuthenticationToken that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }
}
