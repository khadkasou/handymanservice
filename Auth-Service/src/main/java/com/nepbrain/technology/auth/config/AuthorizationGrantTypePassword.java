package com.nepbrain.technology.auth.config;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class AuthorizationGrantTypePassword {

  public static final AuthorizationGrantType GRANT_PASSWORD =
      new AuthorizationGrantType("custom_password");

  private AuthorizationGrantTypePassword() {}
}
