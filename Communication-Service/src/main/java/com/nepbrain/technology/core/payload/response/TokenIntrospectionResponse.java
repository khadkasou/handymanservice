package com.nepbrain.technology.core.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenIntrospectionResponse {
    private boolean active;

    @JsonProperty("sub")
    private String userId;

    @JsonProperty("aud")
    private List<String> aud;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("scope")
    private String scopes;

    private List<String> authorities;

    @JsonProperty("nbf")
    private Long nbf;

    @JsonProperty("iss")
    private String iss;

    @JsonProperty("exp")
    private Long exp;

    @JsonProperty("iat")
    private Long iat;

    @JsonProperty("jti")
    private String jti;

    @JsonProperty("username")
    private String username;

    private Instant issuedAt;
    private Instant expiresAt;
    private String metadata;
}

