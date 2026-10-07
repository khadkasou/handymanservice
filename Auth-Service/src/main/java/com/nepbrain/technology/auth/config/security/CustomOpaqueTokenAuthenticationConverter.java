package com.nepbrain.technology.auth.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOpaqueTokenAuthenticationConverter
    implements OpaqueTokenAuthenticationConverter {

  /**
   * Convert the given opaque token to an {@link Authentication} object.
   *
   * The method first extracts the username and authorities from the given
   * {@link OAuth2AuthenticatedPrincipal}. It then creates a new
   * {@link CustomOAuth2AuthenticatedPrincipal} object with the extracted username
   * and authorities. The method then returns a new {@link BearerTokenAuthentication}
   * object with the created {@link CustomOAuth2AuthenticatedPrincipal} and the given
   * opaque token.
   *
   * @param introspectedToken the opaque token
   * @param authenticatedPrincipal the authenticated principal
   * @return the converted authentication object
   */
  @Override
  public Authentication convert(
      String introspectedToken, OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
    Map<String, Object> attributes = authenticatedPrincipal.getAttributes();

    Object authoritiesObject = authenticatedPrincipal.getAttributes().get("authorities");
    List<String> permissions = new ArrayList<>();
    if (authoritiesObject instanceof Collection<?> authoritiesCollection) {
      permissions =
          authoritiesCollection.stream()
              .filter(String.class::isInstance)
              .map(String.class::cast)
              .toList();
    }

    Collection<? extends GrantedAuthority> authorities =
        permissions.stream().map(SimpleGrantedAuthority::new).toList();

    String username = null;
    if (attributes.containsKey("username")
        && StringUtils.hasText((String) attributes.get("username"))) {
      username = (String) attributes.get("username");
    }

    OAuth2AccessToken accessToken =
        new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            introspectedToken,
            authenticatedPrincipal.getAttribute(IdTokenClaimNames.IAT),
            authenticatedPrincipal.getAttribute(IdTokenClaimNames.EXP));

    CustomOAuth2AuthenticatedPrincipal customOAuth2User =
        new CustomOAuth2AuthenticatedPrincipal(username, authorities, attributes);

    return new BearerTokenAuthentication(
        customOAuth2User, accessToken, customOAuth2User.getAuthorities());
  }
}
