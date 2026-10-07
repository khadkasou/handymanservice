package com.nepbrain.technology.auth.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

final class OAuth2AuthorizationRequestDeserializer
    extends JsonDeserializer<OAuth2AuthorizationRequest> {

  @Override
  public OAuth2AuthorizationRequest deserialize(JsonParser parser, DeserializationContext context)
      throws JsonProcessingException {
    try {
      ObjectMapper mapper = (ObjectMapper) parser.getCodec();
      JsonNode root = mapper.readTree(parser);
      return deserialize(mapper, root);
    } catch (Exception ex) {
      throw new JsonProcessingException("Unable to deserialize OAuth2AuthorizationRequest", ex) {};
    }
  }

  private OAuth2AuthorizationRequest deserialize(ObjectMapper mapper, JsonNode root) {
    AuthorizationGrantType grantType =
        convertAuthorizationGrantType(JsonNodeUtils.findObjectNode(root, "authorizationGrantType"));
    OAuth2AuthorizationRequest.Builder builder = getBuilder(grantType);
    builder.authorizationUri(JsonNodeUtils.findStringValue(root, "authorizationUri"));
    builder.clientId(JsonNodeUtils.findStringValue(root, "clientId"));
    builder.redirectUri(JsonNodeUtils.findStringValue(root, "redirectUri"));
    builder.scopes(JsonNodeUtils.findValue(root, "scopes", JsonNodeUtils.STRING_SET, mapper));
    builder.state(JsonNodeUtils.findStringValue(root, "state"));
    builder.additionalParameters(
        JsonNodeUtils.findValue(
            root, "additionalParameters", JsonNodeUtils.STRING_OBJECT_MAP, mapper));
    builder.authorizationRequestUri(JsonNodeUtils.findStringValue(root, "authorizationRequestUri"));
    builder.attributes(
        JsonNodeUtils.findValue(root, "attributes", JsonNodeUtils.STRING_OBJECT_MAP, mapper));
    return builder.build();
  }

  private OAuth2AuthorizationRequest.Builder getBuilder(AuthorizationGrantType authorizationGrantType) {
    if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationGrantType)) {
      return OAuth2AuthorizationRequest.authorizationCode();
    }
    throw new IllegalArgumentException("Invalid authorizationGrantType: " + authorizationGrantType);
  }

  private static AuthorizationGrantType convertAuthorizationGrantType(JsonNode jsonNode) {
    String value = JsonNodeUtils.findStringValue(jsonNode, "value");
    if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equalsIgnoreCase(value)) {
      return AuthorizationGrantType.AUTHORIZATION_CODE;
    }
    return null;
  }
}

