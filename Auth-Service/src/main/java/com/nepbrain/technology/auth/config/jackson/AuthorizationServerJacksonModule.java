package com.nepbrain.technology.auth.config.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenExchangeActor;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenExchangeCompositeAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;

/**
 * Custom copy of Spring Authorization Server's deprecated Jackson module.
 */
public class AuthorizationServerJacksonModule extends SimpleModule {

  public AuthorizationServerJacksonModule() {
    super(AuthorizationServerJacksonModule.class.getName(), new Version(1, 0, 0, null, null, null));
  }

  @Override
  public void setupModule(SetupContext context) {
    SecurityModuleRegistrar.enableDefaultTyping(context.getOwner());
    context.setMixInAnnotations(
        Collections.unmodifiableMap(Collections.emptyMap()).getClass(),
        UnmodifiableMapMixin.class);
    context.setMixInAnnotations(HashSet.class, HashSetMixin.class);
    context.setMixInAnnotations(LinkedHashSet.class, HashSetMixin.class);
    context.setMixInAnnotations(OAuth2TokenExchangeActor.class, OAuth2TokenExchangeActorMixin.class);
    context.setMixInAnnotations(
        OAuth2TokenExchangeCompositeAuthenticationToken.class,
        OAuth2TokenExchangeCompositeAuthenticationTokenMixin.class);
    context.setMixInAnnotations(OAuth2AuthorizationRequest.class, OAuth2AuthorizationRequestMixin.class);
    context.setMixInAnnotations(Duration.class, DurationMixin.class);
    context.setMixInAnnotations(SignatureAlgorithm.class, JwsAlgorithmMixin.class);
    context.setMixInAnnotations(MacAlgorithm.class, JwsAlgorithmMixin.class);
    context.setMixInAnnotations(OAuth2TokenFormat.class, OAuth2TokenFormatMixin.class);
    context.setMixInAnnotations(String[].class, StringArrayMixin.class);
  }
}

