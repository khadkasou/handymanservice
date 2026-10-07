package com.nepbrain.technology.auth.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.auth.config.jackson.SecurityModuleRegistrar;
import com.nepbrain.technology.auth.config.jackson.AuthorizationServerJacksonModule;
import com.nepbrain.technology.auth.entity.TokenEntity;
import com.nepbrain.technology.auth.enums.UserStatusEnum;
import com.nepbrain.technology.auth.repository.TokenRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class JpaOAuth2AuthorizationService implements OAuth2AuthorizationService {
  private final TokenRepository authorizationRepository;
  private final RegisteredClientRepository registeredClientRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public JpaOAuth2AuthorizationService(
      TokenRepository authorizationRepository,
      RegisteredClientRepository registeredClientRepository) {
    Assert.notNull(authorizationRepository, "authorizationRepository cannot be null");
    Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
    this.authorizationRepository = authorizationRepository;
    this.registeredClientRepository = registeredClientRepository;

    ClassLoader classLoader = JpaOAuth2AuthorizationService.class.getClassLoader();
    List<Module> securityModules = SecurityModuleRegistrar.getModules(classLoader);
    this.objectMapper.registerModules(securityModules);
    this.objectMapper.registerModule(new AuthorizationServerJacksonModule());
  }

  private static AuthorizationGrantType resolveAuthorizationGrantType(
      String authorizationGrantType) {
    if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
      return AuthorizationGrantType.AUTHORIZATION_CODE;
    } else if (AuthorizationGrantType.CLIENT_CREDENTIALS
        .getValue()
        .equals(authorizationGrantType)) {
      return AuthorizationGrantType.CLIENT_CREDENTIALS;
    } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
      return AuthorizationGrantType.REFRESH_TOKEN;
    } else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
      return AuthorizationGrantType.DEVICE_CODE;
    }
    return new AuthorizationGrantType(authorizationGrantType);
  }

  @Override
  public void save(OAuth2Authorization authorization) {
    Assert.notNull(authorization, "authorization cannot be null");
    this.authorizationRepository.save(toEntity(authorization));
  }

  @Override
  public void remove(OAuth2Authorization authorization) {
    Assert.notNull(authorization, "authorization cannot be null");
    this.authorizationRepository.deleteById(authorization.getId());
  }

  @Override
  public OAuth2Authorization findById(String id) {
    Assert.hasText(id, "id cannot be empty");
    return this.authorizationRepository.findById(id).map(this::toObject).orElse(null);
  }

  @Override
  public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
    Assert.hasText(token, "token cannot be empty");

    Optional<TokenEntity> result = Optional.empty();
    if (tokenType == null) {
      result = this.authorizationRepository.findByStateOrAccessTokenValueOrRefreshTokenValue(token);
    } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
      result = this.authorizationRepository.findByAccessTokenValue(token);
    } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
      result = this.authorizationRepository.findByRefreshTokenValue(token);
    }
    return result.map(this::toObject).orElse(null);
  }

  private OAuth2Authorization toObject(TokenEntity entity) {
    RegisteredClient registeredClient =
        this.registeredClientRepository.findById(entity.getRegisteredClientId());
    if (registeredClient == null) {
      throw new DataRetrievalFailureException(
          "The RegisteredClient with id '"
              + entity.getRegisteredClientId()
              + "' was not found in the RegisteredClientRepository.");
    }

    OAuth2Authorization.Builder builder =
        OAuth2Authorization.withRegisteredClient(registeredClient)
            .id(String.valueOf(entity.getId()))
            .principalName(entity.getPrincipalName())
            .authorizationGrantType(
                resolveAuthorizationGrantType(entity.getAuthorizationGrantType()))
            .authorizedScopes(StringUtils.commaDelimitedListToSet(entity.getAuthorizedScopes()))
            .attributes(attributes -> attributes.putAll(parseMap(entity.getAttributes())));
    if (entity.getStatus() != null) {
      builder.attribute(OAuth2ParameterNames.STATE, entity.getStatus());
    }

    if (entity.getAccessTokenValue() != null) {
      OAuth2AccessToken accessToken =
          new OAuth2AccessToken(
              OAuth2AccessToken.TokenType.BEARER,
              entity.getAccessTokenValue(),
              entity.getAccessTokenIssuedAt(),
              entity.getAccessTokenExpiresAt(),
              StringUtils.commaDelimitedListToSet(""));
      builder.token(
          accessToken, metadata -> metadata.putAll(parseMap(entity.getAccessTokenMetadata())));
    }

    if (entity.getRefreshTokenValue() != null) {
      OAuth2RefreshToken refreshToken =
          new OAuth2RefreshToken(
              entity.getRefreshTokenValue(),
              entity.getRefreshTokenIssuedAt(),
              entity.getRefreshTokenExpiresAt());
      builder.token(
          refreshToken, metadata -> metadata.putAll(parseMap(entity.getRefreshTokenMetadata())));
    }

    return builder.build();
  }

  private TokenEntity toEntity(OAuth2Authorization authorization) {
    TokenEntity entity = new TokenEntity();
    entity.setId(authorization.getId());
    entity.setRegisteredClientId(authorization.getRegisteredClientId());
    entity.setPrincipalName(authorization.getPrincipalName());
    entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
    entity.setAuthorizedScopes(
        StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ","));
    entity.setAttributes(writeMap(authorization.getAttributes()));
    entity.setStatus(UserStatusEnum.ACTIVE.name());

    OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
        authorization.getToken(OAuth2AccessToken.class);
    setTokenValues(
        accessToken,
        entity::setAccessTokenValue,
        entity::setAccessTokenIssuedAt,
        entity::setAccessTokenExpiresAt,
        entity::setAccessTokenMetadata);

    OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
        authorization.getToken(OAuth2RefreshToken.class);
    setTokenValues(
        refreshToken,
        entity::setRefreshTokenValue,
        entity::setRefreshTokenIssuedAt,
        entity::setRefreshTokenExpiresAt,
        entity::setRefreshTokenMetadata);

    return entity;
  }

  private void setTokenValues(
      OAuth2Authorization.Token<?> token,
      Consumer<String> tokenValueConsumer,
      Consumer<Instant> issuedAtConsumer,
      Consumer<Instant> expiresAtConsumer,
      Consumer<String> metadataConsumer) {
    if (token != null) {
      OAuth2Token oAuth2Token = token.getToken();
      tokenValueConsumer.accept(oAuth2Token.getTokenValue());
      issuedAtConsumer.accept(oAuth2Token.getIssuedAt());
      expiresAtConsumer.accept(oAuth2Token.getExpiresAt());
      metadataConsumer.accept(writeMap(token.getMetadata()));
    }
  }

  private Map<String, Object> parseMap(String data) {
    try {
      return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }

  private String writeMap(Map<String, Object> metadata) {
    try {
      return this.objectMapper.writeValueAsString(metadata);
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }
}
