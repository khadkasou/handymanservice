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

    @JsonProperty("client_id")
    private String clientId;

    // Map both "sub" and "username" fields to userId
    @JsonProperty("sub")
    private String sub;

    @JsonProperty("username")
    private String username;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("scope")
    private String scopes;

    private List<String> authorities;

    @JsonProperty("iat")
    private Instant issuedAt;

    @JsonProperty("exp")
    private Instant expiresAt;

    private String metadata;

    /** Get user ID from sub, username, or null */
    public String getUserId() {
        if (sub != null && !sub.isEmpty()) {
            return sub;
        }
        if (username != null && !username.isEmpty()) {
            return username;
        }
        return null;
    }
}
