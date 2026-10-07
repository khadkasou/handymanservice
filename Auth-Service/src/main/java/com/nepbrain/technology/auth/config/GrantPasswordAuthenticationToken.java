package com.nepbrain.technology.auth.config;

import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.*;

import static com.nepbrain.technology.auth.config.AuthorizationGrantTypePassword.GRANT_PASSWORD;

@Getter
public class GrantPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String username;
    private final String password;
    private final Set<String> scopes;

    public GrantPasswordAuthenticationToken(Authentication clientPrincipal, String username, String password, @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {
        super(GRANT_PASSWORD, clientPrincipal, additionalParameters);
        Assert.hasText(username, "username cannot be empty");
        Assert.hasText(password, "password cannot be empty");
        this.username = username;
        this.password = password;
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrantPasswordAuthenticationToken that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }



}
