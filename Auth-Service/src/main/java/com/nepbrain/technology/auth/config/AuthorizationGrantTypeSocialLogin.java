package com.nepbrain.technology.auth.config;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

public final class AuthorizationGrantTypeSocialLogin {

    public static final AuthorizationGrantType GRANT_SOCIAL_LOGIN =
            new AuthorizationGrantType("social_login");

    private AuthorizationGrantTypeSocialLogin() {}
}
